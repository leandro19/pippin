package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
public class Assembler {
	
	public static void main(String[] args){
		File input = new File("26e.pasm");
		File output = new File("26e.pexe");
		Assembler.assemble(input, output);
	}

	public static String assemble(File input, File output){
		String returnValue = "success";
		try {
			ArrayList<String> code = new ArrayList<>();
			ArrayList<String> data = new ArrayList<>();
			Scanner scan = new Scanner(input);
			ArrayList<String> inText = new ArrayList<>();
			//put lines of input into inText
			ArrayList<String> outText = new ArrayList<>();
			//put lines of input into inText
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				inText.add(line);
			}
			scan.close();
				String blankLine = null;
				Integer errorIndex = null;
				boolean pastData = false;
				boolean pastHalt = false;
			for(String line : inText){
				if(blankLine == null && line.trim().length() == 0){
					blankLine = line;
				}
				if(!pastHalt){
					pastHalt = line.trim().equals("HALT");
				}
				if(!pastData){
					pastData = line.trim().equals("DATA");
				}
				//blank line error
				//System.out.println("line " + (inText.indexOf(line) + 1) + " length greater than 0: " + (line.length() > 0));
				if(errorIndex == null && blankLine != null && line.trim().length() > 0){
					errorIndex = inText.indexOf(blankLine);
					returnValue = "Error: line " + (errorIndex + 1) + " is a blank line";
				}
				//white space error
				else if (errorIndex == null && line.trim().length() > 0 && (line.charAt(0) == ' '  || line.charAt(0) == '\t')){
					errorIndex = inText.indexOf(line);
					returnValue = "Error: line " + (errorIndex + 1) + " starts with white space";
				}
				//lower-case DATA error
				else if(errorIndex == null && line.trim().toUpperCase().equals("DATA")){
					if(!line.trim().equals("DATA")){
						errorIndex = inText.indexOf(line);
						returnValue = "Error: line " + (errorIndex + 1) + " does not have DATA in upper case";
					}
				}else{
					if(errorIndex == null){
						if(!pastData && line.trim().length() != 0){
							code.add(line);
						}else{
							data.add(line);
						}
					}
				}
			}
			//for(String s: code) System.out.println(s);
			Integer codeErrorIdx = null;
			for(String line: code){
				String[] parts = line.trim().split("\\s+");
				//lower case instruction error
				if(codeErrorIdx == null && !InstructionMap.sourceCodes.contains(parts[0].toUpperCase())){
					codeErrorIdx = inText.indexOf(line);
					returnValue  = "Error: line " + (codeErrorIdx + 1) + " unknown mnemonic.";
				}
				else if(codeErrorIdx == null && InstructionMap.sourceCodes.contains(parts[0].toUpperCase())
						 && !InstructionMap.sourceCodes.contains(parts[0])){
					codeErrorIdx = inText.indexOf(line);
					returnValue  = "Error: line " + (codeErrorIdx + 1) + " does not have the instruction mnemonic in upper case";
				}
				//illegal argument error
				else if(codeErrorIdx == null && InstructionMap.noArgument.contains(parts[0])
						 && parts.length != 1){
					codeErrorIdx = inText.indexOf(line);
					returnValue = "Error: line " + (codeErrorIdx + 1) + " has an illegal argument";
				}
				//too few or too many arguments
				else if(codeErrorIdx == null && !InstructionMap.noArgument.contains(parts[0])
						 && (parts.length == 1 || parts.length > 2)){
					codeErrorIdx = inText.indexOf(line);
					if(parts.length == 1) returnValue = "Error: line " + (codeErrorIdx + 1) + " is missing an argument";
					if(parts.length > 2) returnValue = "Error: line " + (codeErrorIdx + 1) + " has more than one argument";
				}
				else{
					if(codeErrorIdx == null){
						if(parts.length == 2){
							if(parts[1].startsWith("#")){
							    //incorrect instruction mnemonic
								if(codeErrorIdx == null && !InstructionMap.immediateOK.contains(parts[0])){
									codeErrorIdx = inText.indexOf(line);
									returnValue = "Error: line " + (codeErrorIdx + 1) + " does not have a correct instruction mnemonic";
								}
								else{
									parts[1] = parts[1].substring(1);
									if(parts[0].equals("JUMP")){
										parts[0] = "JMPI";
									}else if(parts[0].equals("JMPZ")){
										parts[0] = "JMZI";
									}else{
										parts[0] += "I";
									}
								}
							}
							else if(parts[1].startsWith("&")){
							    //incorrect instruction mnemonic
								if(codeErrorIdx == null && !InstructionMap.indirectOK.contains(parts[0])){
									codeErrorIdx = inText.indexOf(line);
									returnValue = "Error: line " + (codeErrorIdx + 1) + " does not have a correct instruction mnemonic";
								}else{
									parts[1] = parts[1].substring(1);
									if(parts[0].equals("JUMP"))
									{
										parts[0] = "JMPN";
									}
									else
									{
										parts[0] += "N";
									}
									
								}
							}
							if(codeErrorIdx == null){
								int arg = 0;
								//non-numeric argument
								try {
									arg = Integer.parseInt(parts[1],16); //<<<<< CORRECTION
								} catch (NumberFormatException e) {
									codeErrorIdx = inText.indexOf(line);
									returnValue = "Error: line " + (codeErrorIdx + 1) + " does not have a numeric argument";
								}
							}
						}
						//for(String s: parts) System.out.println(s);
						int opcode = InstructionMap.opcode.get(parts[0]);
						if(parts.length == 1){
							outText.add(Integer.toHexString(opcode).toUpperCase() + " 0");
						}else if(parts.length == 2){
							outText.add(Integer.toHexString(opcode).toUpperCase() + " " + parts[1]);
						}
					}
				}
			}
			Integer dataErrorIdx = null;
			for(String line: data){
				if(codeErrorIdx == null && dataErrorIdx == null && pastData && line.trim().length() > 0){
					String[] parts = line.trim().split("\\s+");
					int arg = 0; 
					//non-numeric data
					try {
						arg = Integer.parseInt(parts[0],16); //<<<<< CORRECTION
					} catch (NumberFormatException e) {
						dataErrorIdx = inText.indexOf(line);
						returnValue = "Error: line " + (dataErrorIdx + 1) + " does not have a numeric argument";
					}
					try {
						arg = Integer.parseInt(parts[1],16); //<<<<< CORRECTION
					} catch (NumberFormatException e) {
						dataErrorIdx = inText.indexOf(line);
						returnValue = "Error: line " + (dataErrorIdx + 1) + " does not have a numeric argument";
					}
					if(parts.length > 2){
						dataErrorIdx = inText.indexOf(line);
						returnValue = "Error: line " + (dataErrorIdx + 1) + " has a bad memory format.";
					}
				}
			}
			outText.add("-1");
			outText.addAll(data);
			if(errorIndex == null){
				PrintWriter out = new PrintWriter(output);
				for(String line:outText){
					out.println(line);
				}
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
}