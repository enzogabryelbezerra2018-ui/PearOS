#include <packages/Calendario/__app_main__.c>
#include <hardware/pear_os_open_source_project/build.prop>
void panic_linux(const char* err) {
    printf("------------[ KERNEL PANIC ]------------\n");
    printf("Kernel panic - not syncing: %s\n", err);
    printf("\n");

    printf("CPU: 0 PID: 1 Comm: init\n");
    printf("Hardware name: AsmUi/BOMJINIKA\n");
    printf("\n");

    printf("Call Trace:\n");
    printf(" [<ffffffff81001234>] dump_stack+0x78/0x90\n");
    printf(" [<ffffffff8100abcd>] panic+0x1a0/0x220\n");
    printf(" [<ffffffff8100cafe>] kernel_start+0x10/0x20\n");
    printf("\n");

    printf("RIP: 0010:deadbeefcafebabe RSP: 0018:0000000000000000\n");
    printf("RAX: 0000000000000001 RBX: 0000000000000000\n");
    printf("RCX: ffffffff00000000 RDX: 0000000000000000\n");
    printf("\n");

    printf("Kernel Offset: 0x123456 from 0xffffffffff000000\n");
    printf("-----------------------------------------------\n");

    for(;;);
}
void panic_windows(const char* err) {
    printf("============================================================\n");
    printf("                     :(  Seu PC encontrou um problema       \n");
    printf("============================================================\n");
    printf("\n");

    printf("O sistema precisa ser reiniciado.\n");
    printf("Código do erro: %s\n", err);
    printf("\n");

    printf("Coletando algumas informações...\n");
    printf("0%% concluído\n");
    printf("\n");

    printf("Para mais detalhes visite:\n");
    printf("https://asmui.system/stopcode\n");
    printf("\n");

    printf("STOP_CODE: SYSTEM_THREAD_EXCEPTION_NOT_HANDLED\n");
    printf("\n");

    printf("============================================================\n");

    for(;;);
}
void panic_android(const char* err) {
    printf("------------------------------------------------------------\n");
    printf("                     ANDROID KERNEL PANIC                   \n");
    printf("------------------------------------------------------------\n");
    printf("\n");

    printf("Unable to handle kernel NULL pointer dereference at address 0x00000000\n");
    printf("Fatal exception in interrupt\n");
    printf("\n");

    printf("Error: %s\n", err);
    printf("\n");

    printf("CPU: 0    Tainted: G W     (AsmUi Kernel BOMJINIKA)\n");
    printf("PC is at 0xDEADBEEF\n");
    printf("LR is at 0xCAFEBABE\n");
    printf("\n");

    printf("SP: 0x00123456  FP: 0x00123400\n");
    printf("r0 : 00000000  r1 : FFFFFFFF  r2 : 0000AAAA  r3 : BBBB0000\n");
    printf("r4 : 11111111  r5 : 22222222  r6 : 33333333  r7 : 44444444\n");
    printf("\n");

    printf("---- SYSTEM HALTED ----\n");

    for(;;);
}
void panic_scifi(const char* err) {
    printf("############################################################\n");
    printf("#                  SYSTEM CORE COLLAPSE                     #\n");
    printf("############################################################\n");
    printf("\n");

    printf("> ERROR: %s\n", err);
    printf("> CRITICAL FAILURE IN QUANTUM EXECUTION ENGINE\n");
    printf("> MEMORY GRID DESYNCHRONIZED\n");
    printf("\n");

    printf("> Attempting auto-realignment........ FAILED\n");
    printf("> Attempting fallback boot layer...... FAILED\n");
    printf("> Attempting recovery channel......... FAILED\n");
    printf("\n");

    printf("> SYSTEM STATUS: IRREVERSIBLE\n");
    printf("> LOCKING ALL THREADS\n");
    printf("\n");

    printf("################ SYSTEM FREEZE ENABLED #####################\n");

    for(;;);
}
