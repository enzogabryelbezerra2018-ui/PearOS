/*
    bootloader_stb.h - Bootloader completo estilo STB (single-file library)
    Versão: 0.1 - Educacional

    Para usar:
        #define BOOTLOADER_STB_IMPLEMENTATION
        #include "bootloader_stb.h"

    Este arquivo contém:
        - Rotinas de saída (VGA + Serial)
        - Leitor simplificado de FAT32
        - Parser simples de arquivos ELF
        - Loader de kernel
        - Jump de execução
        - Função principal bootloader_start()

    O objetivo é mostrar um “mega header” inspirado em stb_image.h,
    totalmente independente, sem bibliotecas externas.
*/

#ifndef BOOTLOADER_STB_H
#define BOOTLOADER_STB_H

// =============================
// CONFIGURAÇÕES GERAIS
// =============================
#define BL_VGA_ADDR          0xB8000
#define BL_SERIAL_PORT       0x3F8
#define BL_KERNEL_LOAD_ADDR  0x100000
#define BL_MAX_FILENAME      64

// =============================
// ESTRUTURAS PÚBLICAS
// =============================
typedef struct {
    char name[BL_MAX_FILENAME];
    unsigned int size;
    unsigned int cluster;
} bl_file_info;

typedef struct {
    unsigned int entry_point;
    unsigned int text_offset;
    unsigned int text_size;
    unsigned int data_offset;
    unsigned int data_size;
} bl_elf_info;

// =============================
// API PÚBLICA
// =============================

void bl_print(const char *msg);
void bl_print_hex(unsigned int v);
void bl_vga_clear();
void bl_serial_init();

int  bl_fat32_mount();
int  bl_fat32_find(const char *name, bl_file_info *out);
int  bl_fat32_read(const bl_file_info *file, void *dst);

int  bl_elf_parse(const void *data, bl_elf_info *out);
int  bl_kernel_load(const char *filename);
void bl_kernel_jump(unsigned int entry);

void bootloader_start();


// =================================================
// IMPLEMENTAÇÃO
// =================================================
#ifdef BOOTLOADER_STB_IMPLEMENTATION
// =============================
//  VGA TEXT MODE
// =============================
static unsigned short *const bl_vga = (unsigned short *)BL_VGA_ADDR;
static int bl_vga_row = 0, bl_vga_col = 0;
static const unsigned char bl_vga_color = 0x0F;

void bl_vga_putc(char c) {
    if (c == '\n') {
        bl_vga_row++;
        bl_vga_col = 0;
        return;
    }
    bl_vga[bl_vga_row * 80 + bl_vga_col] = (bl_vga_color << 8) | c;
    bl_vga_col++;
    if (bl_vga_col >= 80) {
        bl_vga_row++;
        bl_vga_col = 0;
    }
}

void bl_vga_clear() {
    for (int i = 0; i < 80 * 25; i++)
        bl_vga[i] = (bl_vga_color << 8) | ' ';
    bl_vga_row = 0;
    bl_vga_col = 0;
}
// =============================
// PORTA SERIAL
// =============================
static inline void bl_outb(unsigned short port, unsigned char val) {
    asm volatile("outb %0,%1" : : "a"(val), "Nd"(port));
}

static inline unsigned char bl_inb(unsigned short port) {
    unsigned char r;
    asm volatile("inb %1,%0" : "=a"(r) : "Nd"(port));
    return r;
}

void bl_serial_init() {
    bl_outb(BL_SERIAL_PORT + 1, 0x00);
    bl_outb(BL_SERIAL_PORT + 3, 0x80);
    bl_outb(BL_SERIAL_PORT + 0, 0x03);
    bl_outb(BL_SERIAL_PORT + 1, 0x00);
    bl_outb(BL_SERIAL_PORT + 3, 0x03);
    bl_outb(BL_SERIAL_PORT + 2, 0xC7);
    bl_outb(BL_SERIAL_PORT + 4, 0x0B);
}

void bl_serial_putc(char c) {
    while ((bl_inb(BL_SERIAL_PORT + 5) & 0x20) == 0);
    bl_outb(BL_SERIAL_PORT, c);
}
void bl_print(const char *msg) {
    while (*msg) {
        bl_vga_putc(*msg);
        bl_serial_putc(*msg);
        msg++;
    }
}

void bl_print_hex(unsigned int v) {
    const char *hex = "0123456789ABCDEF";
    bl_print("0x");
    for (int i = 28; i >= 0; i -= 4)
        bl_vga_putc(hex[(v >> i) & 0xF]), bl_serial_putc(hex[(v >> i) & 0xF]);
}
// ===============================================
// FAT32 SIMPLIFICADO
// ===============================================
static unsigned int bl_fat32_lba_begin;
static unsigned int bl_fat32_cluster_begin;
static unsigned int bl_fat32_sectors_per_cluster;
static unsigned int bl_fat32_fat_begin;

typedef struct __attribute__((packed)) {
    unsigned char  jmp[3];
    unsigned char  oem[8];
    unsigned short bytes_per_sector;
    unsigned char  sectors_per_cluster;
    unsigned short reserved;
    unsigned char  fats;
    unsigned short unused1;
    unsigned short unused2;
    unsigned int   sectors_per_fat;
    unsigned int   root_cluster;
} bl_fat32_bpb;

static bl_fat32_bpb bl_bpb;

extern void bl_disk_read(unsigned int lba, unsigned int count, void *dst); 
// Você pode implementar no real hardware ou deixar QEMU fazer (int 0x13)

int bl_fat32_mount() {
    bl_disk_read(0, 1, &bl_bpb);

    bl_fat32_lba_begin = 2048; // SUPOSIÇÃO
    bl_fat32_sectors_per_cluster = bl_bpb.sectors_per_cluster;
    bl_fat32_fat_begin = bl_fat32_lba_begin + bl_bpb.reserved;
    bl_fat32_cluster_begin = bl_fat32_fat_begin + bl_bpb.sectors_per_fat * bl_bpb.fats;

    bl_print("FAT32 montado\n");
    return 1;
}
// ===============================================
// PARSER ELF SIMPLES
// ===============================================
typedef struct {
    unsigned char magic[4];
    unsigned char cls;
    unsigned char endian;
    unsigned char version;
    unsigned char abi;
    unsigned char pad[8];
    unsigned short type;
    unsigned short machine;
    unsigned int version2;
    unsigned int entry;
    unsigned int phoff;
    unsigned int shoff;
    unsigned int flags;
    unsigned short ehsize;
    unsigned short phentsize;
    unsigned short phnum;
} bl_elf_ehdr;

typedef struct {
    unsigned int type;
    unsigned int offset;
    unsigned int vaddr;
    unsigned int paddr;
    unsigned int filesz;
    unsigned int memsz;
    unsigned int flags;
    unsigned int align;
} bl_elf_phdr;

int bl_elf_parse(const void *data, bl_elf_info *out) {
    const bl_elf_ehdr *eh = (const bl_elf_ehdr*)data;
    if (eh->magic[0] != 0x7F || eh->magic[1] != 'E' || eh->magic[2] != 'L' || eh->magic[3] != 'F')
        return 0;

    out->entry_point = eh->entry;
    return 1;
}
int bl_kernel_load(const char *filename) {
    bl_file_info info;
    if (!bl_fat32_find(filename, &info)) {
        bl_print("Arquivo não encontrado: ");
        bl_print(filename);
        bl_print("\n");
        return 0;
    }

    bl_print("Carregando kernel...\n");

    void *dst = (void*)BL_KERNEL_LOAD_ADDR;
    bl_fat32_read(&info, dst);

    bl_elf_info elf;
    if (!bl_elf_parse(dst, &elf)) {
        bl_print("Kernel ELF inválido!\n");
        return 0;
    }

    bl_print("Kernel carregado. Entry = ");
    bl_print_hex(elf.entry_point);
    bl_print("\n");

    return elf.entry_point;
}
void bl_kernel_jump(unsigned int entry) {
    bl_print("Iniciando kernel...\n");

    void (*kentry)(void) = (void (*)(void))entry;
    kentry();
}
void bootloader_start() {
    bl_vga_clear();
    bl_serial_init();

    bl_print("=== STB BOOTLOADER ===\n");

    bl_fat32_mount();

    unsigned int entry = bl_kernel_load("KERNEL.ELF");
    if (!entry) {
        bl_print("Erro carregando kernel.\n");
        while (1);
    }

    bl_kernel_jump(entry);
}

#endif // IMPLEMENTATION
#endif // HEADER
