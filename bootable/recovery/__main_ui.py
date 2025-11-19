# ======================================================================
#  PearOS / AsmUi Recovery
#  __main_ui.py - Interface gráfica do modo Recovery
#  Desenvolvido por: Enzo Gabryel (AOST / PearCar / AsmUi Project)
# ======================================================================

import time
import sys
import os
from __logic import RecoveryCore, RecoveryLog

# ======================================================================
#  CLASSES DE INTERFACE
# ======================================================================

class Color:
    RED = "\033[91m"
    GREEN = "\033[92m"
    CYAN = "\033[96m"
    YELLOW = "\033[93m"
    BLUE = "\033[94m"
    WHITE = "\033[97m"
    RESET = "\033[0m"


class RecoveryUI:

    def __init__(self):
        self.core = RecoveryCore()
        self.log = RecoveryLog()
        self.options = [
            "Reiniciar sistema",
            "Wipe Data / Factory Reset",
            "Wipe Cache",
            "Aplicar Update via USB",
            "Aplicar Update via ZIP",
            "Log do Sistema",
            "Desligar"
        ]

    # ==================================================================
    #  ANIMAÇÃO DE SPLASH DO RECOVERY
    # ==================================================================
    def splash(self):
        os.system("clear")
        logo = [
            "██████╗ ███████╗ █████╗ ██████╗  ██████╗ ███████╗",
            "██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔═████╗██╔════╝",
            "██████╔╝█████╗  ███████║██████╔╝██║██╔██║███████╗",
            "██╔═══╝ ██╔══╝  ██╔══██║██╔══██╗████╔╝██║╚════██║",
            "██║     ███████╗██║  ██║██║  ██║╚██████╔╝███████║",
            "╚═╝     ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝",
            "",
            "            PearOS / AsmUi Recovery 5.0",
        ]
        for line in logo:
            print(Color.CYAN + line + Color.RESET)
            time.sleep(0.05)

        print()
        time.sleep(0.7)

    # ==================================================================
    #  MENU PRINCIPAL
    # ==================================================================
    def main_menu(self):
        while True:
            os.system("clear")
            self.log.write("Exibindo menu principal")

            print(Color.GREEN + "=== RECOVERY ===" + Color.RESET)
            print()

            for i, opt in enumerate(self.options):
                print(f"{Color.WHITE}{i+1}. {opt}{Color.RESET}")

            print()
            choice = input(Color.YELLOW + "Escolha uma opção: " + Color.RESET)

            if not choice.isdigit():
                continue

            choice = int(choice)

            match choice:
                case 1: self.core.reboot()
                case 2: self.core.wipe_data()
                case 3: self.core.wipe_cache()
                case 4: self.core.apply_update_usb()
                case 5: self.core.apply_update_zip()
                case 6: self.show_log()
                case 7: self.core.shutdown()

    # ==================================================================
    #  EXIBIR ARQUIVO DE LOG
    # ==================================================================
    def show_log(self):
        os.system("clear")
        print(Color.BLUE + "=== LOG DO SISTEMA ===\n" + Color.RESET)

        with open("recovery.log", "r") as f:
            for line in f.readlines():
                print(Color.WHITE + line + Color.RESET, end="")
        
        print()
        input(Color.YELLOW + "\nPressione ENTER para voltar..." + Color.RESET)



# ======================================================================
#  INICIALIZAÇÃO DO PROGRAMA
# ======================================================================

if __name__ == "__main__":
    ui = RecoveryUI()
    ui.splash()
    ui.main_menu()
