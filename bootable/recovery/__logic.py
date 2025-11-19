# ======================================================================
#  PearOS / AsmUi Recovery
#  __logic.py - Funções lógicas do modo Recovery
#  Desenvolvido por: Enzo Gabryel (AOST / PearCar / AsmUi Project)
# ======================================================================

import os
import time

# ======================================================================
#  SISTEMA DE LOG
# ======================================================================

class RecoveryLog:
    def write(self, text):
        with open("recovery.log", "a") as f:
            f.write(f"[RECOVERY] {text}\n")

# ======================================================================
#  LÓGICA PRINCIPAL
# ======================================================================

class RecoveryCore:

    def __init__(self):
        self.log = RecoveryLog()

    # ================================================================
    #  REINICIAR SISTEMA
    # ================================================================
    def reboot(self):
        self.log.write("Reiniciando o sistema...")
        print("Reiniciando...")
        time.sleep(1)
        os.system("reboot")

    # ================================================================
    #  WIPE DATA / FACTORY RESET
    # ================================================================
    def wipe_data(self):
        self.log.write("Executando Wipe Data / Factory Reset")
        print("Apagando dados do usuário...")
        time.sleep(2)
        os.system("rm -rf /data/user")
        print("Concluído.")
        time.sleep(1)

    # ================================================================
    #  WIPE CACHE
    # ================================================================
    def wipe_cache(self):
        self.log.write("Limpando cache do sistema")
        print("Limpando /cache...")
        time.sleep(1)
        os.system("rm -rf /cache/*")
        print("OK.")
        time.sleep(1)

    # ================================================================
    #  UPDATE VIA USB
    # ================================================================
    def apply_update_usb(self):
        self.log.write("Aguardando update via USB")
        print("Procurando atualização em /mnt/usb/update.zip...")
        time.sleep(1)

        if os.path.exists("/mnt/usb/update.zip"):
            print("Update encontrado! Instalando...")
            self.install_update("/mnt/usb/update.zip")
        else:
            print("Nenhum update encontrado.")
            time.sleep(1)

    # ================================================================
    #  UPDATE VIA ZIP
    # ================================================================
    def apply_update_zip(self):
        self.log.write("Atualização manual via ZIP")
        path = input("Caminho do arquivo ZIP: ")

        if os.path.exists(path):
            print("Instalando...")
            self.install_update(path)
        else:
            print("Arquivo não existe.")
            time.sleep(1)

    # ================================================================
    #  INSTALAR UPDATE
    # ================================================================
    def install_update(self, path):
        self.log.write(f"Instalando atualização: {path}")
        print("Extraindo pacote...")
        time.sleep(2)
        os.system(f"unzip -o {path} -d /system")
        print("Atualização concluída!")
        time.sleep(1)

    # ================================================================
    #  DESLIGAR
    # ================================================================
    def shutdown(self):
        self.log.write("Desligando sistema")
        print("Desligando...")
        time.sleep(1)
        os.system("poweroff")
