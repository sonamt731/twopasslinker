

import java.util.*;

public class twopasslinker {

	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		
		int nummods = input.nextInt();
		int count = 0; //initialized count
		int modnum = 0; //module we are in
		
		HashMap<String, int[]> map = new HashMap<>(); //to store the definitions 
		//the integer array will store the defined value and the module is was defined in
		ArrayList<int[]> addresses = new ArrayList<int[]>(nummods); //this will store the addresses in each module
		ArrayList<String[]> usageorder = new ArrayList<String[]>(); //stores the variables that are used in the order they are used 
		ArrayList<int[]> startaddressesList = new ArrayList<int[]>();
		ArrayList<Integer> endDigits = new ArrayList<Integer>(); 
		ArrayList<String> multdefined = new ArrayList<String>(); //stores the values that were multiply defined 
		int[] numOfAddresses = new int[nummods]; //this stores the number of addresses in each module
		HashSet<String> hash = new HashSet<String>(); //stores the used addresses (integer addresses)
		ArrayList<int[]> originaladdress = new ArrayList<int[]>(nummods); //stores the original addresses to be used when error checking
  
		while (nummods > 0) {
			int firstline = input.nextInt();
			
			//case of a definition
			if (firstline!=0) {
					for (int num1 = 0; num1<firstline; num1++) {
						String newval = input.next(); //stores variable name
						
						//Arbitrary limit if the symbol exceeds 8 characters
						if(newval.length()>8) {
							System.out.println("Your input of "+ newval+ " exceeds the 8 character limit.");
							System.out.println("Program now terminating...");
							System.exit(0); //terminate the program
						}
						int definedAt = input.nextInt();
						if (!map.containsKey(newval)) { //checks to see if the variable has already been defined
							int[] curr = new int[3]; 
							//first element stores defined value, second element is module number, third element is count
							curr[0] = definedAt+count;
							curr[1]=modnum;
							curr[2] = count;
							map.put(newval, curr);
						}
						else {
							multdefined.add(newval); //adds to error array to assure that an error is printed saying the variable was defined multiple times
						}
					}
			}
			
			int secondline = input.nextInt(); //the number of addresses in this module - add to array
			String[] use = new String[secondline]; //storing this modules used variables
			int[] start = new int[secondline]; //storing this modules used memory addresses
			
			//case of usage
			if (secondline!=0) {
				
				for(int num2 = 0; num2<secondline; num2++) {
					 use[num2]=input.next();
					 start[num2] = input.nextInt();
					
				}
			}
			//stores the string that is used - adds an empty list in the case that this module did not have actions
			usageorder.add(use);
			startaddressesList.add(start);
			
			//number of addresses in the module -line 3
			int numAddresses = input.nextInt();
			numOfAddresses[modnum] = numAddresses; //store the number of addresses in this module

			
				
			//temp storage of integers 
			int[] temp = new int[numAddresses];
			int[] temp2 = new int[numAddresses]; //storing original addresses - Java is pass by reference - make sure it isnt modified
			for(int i = 0; i < numAddresses; i++) {
				int tempAddVal = input.nextInt();
				endDigits.add(tempAddVal%10); //this gets the rightmost (5th digit)
				temp[i] = tempAddVal;
				temp2[i]=tempAddVal;
			}
			addresses.add(temp);
			originaladdress.add(temp2);
			count += numAddresses; //will store the total number of addresses 
			nummods-=1;
			
			modnum++;	
		}
		
		//sort list of key names - used to output System Table and Warnings
		String[] names = new String[map.size()];
		int c = 0;
		for(String name: map.keySet()) {
			names[c++] = name;
		}
		
		Arrays.sort(names);
	
		
		
		System.out.println("System Table");
		for(String var: names) {
            String key = var;
            int stored = map.get(key)[0];
            int mod = map.get(key)[1];
            int base = map.get(key)[2];
            
            //case if defined multiply 
            if(multdefined.contains(key)) {
            		System.out.println(key + " = " + stored +" Error: This variable is multiply defined; first value used");
            }
            
            //case if address appearing in a definition exceeds the size of the module 
            else if((numOfAddresses[mod]-1) < (stored - base)) { 
            		//update value stored to 0 
            		int newstored = 0+base;
            		int[] temp = {newstored, mod, base};
            		map.put(key, temp);
            		System.out.println(key + " = " + newstored + " Error: The definition of "+ key+" is outside module "+ mod+"; zero (relative) used");
            }
            
            else {
            		System.out.println(key + " = " + stored);  
            }
			
		}
	
		
		
		//beginning our 2nd pass -- relocating relative addresses and resolving external references 
		System.out.println("");
		System.out.println("Memory Map");
		
		String substr = "";
		String[] warnings = new String[count]; //for the characters that are used but not defined
		int size = 0; //will accumulate number of addresses in the module
		
		//these 3 loops handles the cases of the last digit being 4
		for(int k = 0; k<usageorder.size();k++) {
			for(int j = 0; j<usageorder.get(k).length; j++) {
				int lookup = startaddressesList.get(k)[j]; //stores the index of the variable used s
				while(!substr.equals("777")) {
					substr = Integer.toString(addresses.get(k)[lookup]); 
					//add the used address to the hashset
					hash.add(substr);
					int firstdig = Integer.parseInt(substr.substring(0,1));
					
					//restore the value of the address if it used 
					
					if(map.containsKey(usageorder.get(k)[j])) {
						addresses.get(k)[lookup] = firstdig*1000+(map.get(usageorder.get(k)[j])[0]); //adds the value stored at the key which is used at this address
						substr = substr.substring(1, 4); //next index to visit
						lookup = Integer.parseInt(substr);
					}
					else { //case that the variable is not defined, zero is used
						addresses.get(k)[lookup] = firstdig*1000+0; //lets say 0 is stored 
						warnings[size+lookup]=usageorder.get(k)[j]; //store the character that is used but not defined
						substr = substr.substring(1, 4); //next index to visit
						lookup = Integer.parseInt(substr);
					}
				}
				substr=""; //reset for next iteration
			}
			size += addresses.get(k).length;

		}
		
		
		
		int line = 0;
		int lengthOfMod = 0;
		int index = 0;
		int addressNum = 0;
		
		
		for(int nums = 0; nums<addresses.size();nums++) {
			lengthOfMod = addresses.get(nums).length;
			for(int p = 0; p<lengthOfMod; p++) {
				String address = (Integer.toString(addresses.get(nums)[p])); 
				String original = Integer.toString(originaladdress.get(nums)[p]);
				
				//case if immediate address but used 
				if(endDigits.get(index)==1 && hash.contains(original)) { //last digit is 1 and it is in the hashset containing the used addresses
					System.out.print(index + ":\t"+ address.substring(0,4));
					System.out.println(" Error: Immediate address on use list; treated as External.");
					hash.remove(original); //in case of repeats
					
				}
				
				else if (endDigits.get(index)==3) { //if last digit is a 3
					addressNum = Integer.parseInt(address.substring(0,4)) + line; 
					addresses.get(nums)[p] = addressNum; //update value in our arraylist
					System.out.println(index + ":\t"+addressNum);
				}
				
				//case if external address but not used
				else if (endDigits.get(index)==4 && !hash.contains(original)) {
					System.out.print(index + ":\t"+ address.substring(0,4));
					System.out.println(" Error: E type address not on use chain; treated as I type.");
				}
				
				//case if the absolute address exceeds the size of the machine (200 characters)
				else if(endDigits.get(index)==2 && (Integer.parseInt(address.substring(1,4))>199)){
					address = address.substring(0,1)+"199";
					System.out.print(index + ":\t"+ address);
					System.out.println(" Error: The absolute address exceeds the size of the machine.");
				}
				
				else {
					System.out.print(index + ":\t"+ address.substring(0,4));
					//if the warnings array holds null at that index --> the variable is defined
					if(warnings[index]!=null){ //have to make sure null is not stored otherwise would yield a null pointer
						System.out.print(" Error: " + warnings[index]+ " is not defined; zero used.");
					}
					System.out.println();
					
				}
				index++;
			}
			line+=lengthOfMod;	
		}
		
		System.out.println();
		
		//Code for Warnings when variables are defined but not used
		for (String name: names){
			boolean found = false;
            for (int p = 0; p < usageorder.size(); p++) {
            		for(int q = 0; q < usageorder.get(p).length; q++) {
            			if (usageorder.get(p)[q].equals(name)) {
            				found = true; //we are using the variable 
            				break;
            			}
            		}
            }
            if (!found) { //variable not found
            		System.out.println("Warning: " + name+" was defined in module "+ map.get(name)[1]+" but never used");
            }
            
		}
		
		input.close();

	}

}
