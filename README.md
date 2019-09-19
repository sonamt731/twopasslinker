# twopasslinker

This project implements a two pass linker in Java. 
The first pass uses Scanner to read user input and store data in various data structures. Also the values of declared variables are computed. 
The second pass updates addresses based on base addresses and the right most digits of each address. 

To Run the Code.. 
- Once the user is on my crackle1 i6 account, the user must type cd twopasslinker. This folder has the Two Pass Linker project code. 
1) In order to compile the code the user must enter javac twopasslinker.java 
2) To run the code the user must type java twopasslinker
3) At this stage, the user can enter any data and press enter to get the desired results. 


My Program user scanner to go through and store user input. In this case, the input must be entered in the desire order as specified in the project spec. 

Errors Accounted For:
- If a symbol is defined but not used, print a warning message and continue.
- If a symbol is multiply defined, print an error message and use the value given in the first definition.
- If a symbol is used but not defined, print an error message and use the value zero.
- If multiple symbols are listed as used in the same instruction, print an error message and ignore all but the last usage
given.
- If an address appearing in a definition exceeds the size of the module, print an error message and treat the address
as 0 (relative).
- If an immediate address (i.e., type 1) appears on a use list, print an error message and treat the address as external
(i.e., type 4).
- If an external address is not on a use list, print an error message and treat it as an immediate address.
- If an absolute address exceeds the size of the machine, print an error message and use the largest legal value. (case of exceeding 200)

