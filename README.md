"# Simpletron"

This repo contains the implementation of Simpletron with GUI using javaFX.

************************************************* What is Simpletron? ********************************************************

SIMPLETRON is a computer (through the technique of software-based simulation) on which you can execute your machine-language programs.
The Simpletron runs programs written in the only language it directly understands: Simpletron Machine Language (SML).

The Simpletron is equipped with a 100-word memory, and these words are referenced by their location numbers 00, 01, …, 99.
Before running an SML program, we must load, or place, the program into memory. The first instruction (or statement) of every SML program is always placed
in location 00. The simulator will start executing at this location.

Each location in the Simpletron’s memory may contain an instruction, a data value used by a program or an unused (and so undefined) area of memory.

• The first two digits of each SML instruction are the operation code specifying the operation to be performed.
• The last two digits of an SML instruction are the operand—the address of the memory location containing the word to which the operation applies.

SML operation codes are summarized below.

Operation code                                                         Meaning

→ Input/output operations:
• final int READ = 10;                     Read a word from the keyboard into a specific location in memory.
• final int WRITE = 11;                    Write a word from a specific location in memory to the screen.

→ Load/store operations:
• final int LOAD = 20;                     Load a word from a specific location in memory into the accumulator.
• final int STORE = 21;                    Store a word from the accumulator into a specific location in memory.

→ Arithmetic operations:
• final int ADD = 30;                     Add a word from a specific location in memory to the word in the accumulator.
• final int SUBTRACT = 31;                Subtract a word from a specific location in memory from the word in the accumulator.
• final int DIVIDE = 32;                  Divide a word from a specific location in memory into the word in the accumulator.
• final int MULTIPLY = 33;                Multiply a word from a specific location in memory by the word in the accumulator.

→ Transfer-of-control operations:
• final int BRANCH = 40;                 Branch to a specific location in memory.
• final int BRANCHNEG = 41;              Branch to a specific location in memory if the accumulator is negative.
• final int BRANCHZERO = 42;             Branch to a specific location in memory if the accumulator is zero.
• final int HALT = 43;                   Halt. The program has completed its task.
