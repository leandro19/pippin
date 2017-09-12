package project;

import java.util.Map;
import java.util.TreeMap;
//UPDATE A TON SOME ERRORS
public class MachineModel
{
	public Map<Integer, Instruction> INSTRUCTIONS = new TreeMap<>();
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private Code code = new Code();
	private HaltCallback callback;
	private Job[] jobs = new Job[2];
	private Job currentJob;
	public MachineModel()
	{
		this(() -> System.exit(0));
	}
	public MachineModel(HaltCallback callback)
	{
		this.callback = callback;
        //INSTRUCTION_MAP entry for "ADDI"
        INSTRUCTIONS.put(0xA, arg -> 
        {
            cpu.setAccumulator(cpu.getAccumulator() + arg);
            cpu.incrementIP();
        });

        //INSTRUCTION_MAP entry for "ADD"
        INSTRUCTIONS.put(0xB, arg -> 
        {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            cpu.setAccumulator(cpu.getAccumulator() + arg1);
            cpu.incrementIP();
    		cpu.getMemoryBase();
        });

        //INSTRUCTION_MAP entry for "ADDN"
        INSTRUCTIONS.put(0xC, arg -> 
        {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            int arg2 = memory.getData(cpu.getMemoryBase()+arg1);
            cpu.setAccumulator(cpu.getAccumulator() + arg2);
            cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "SUBI"
        INSTRUCTIONS.put(0xD, arg ->
        {
        	cpu.setAccumulator(cpu.getAccumulator() - arg);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "SUB"
        INSTRUCTIONS.put(0xE, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	cpu.setAccumulator(cpu.getAccumulator() - arg1);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "SUBN"
        INSTRUCTIONS.put(0xF, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	int arg2 = memory.getData(cpu.getMemoryBase() + arg1);
        	cpu.setAccumulator(cpu.getAccumulator() - arg2);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "MULI"
        INSTRUCTIONS.put(0x10, arg ->
        {
        	cpu.setAccumulator(cpu.getAccumulator() * arg);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "MUL"
        INSTRUCTIONS.put(0x11, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	cpu.setAccumulator(cpu.getAccumulator() * arg1);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "MULN"
        INSTRUCTIONS.put(0x12, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	int arg2 = memory.getData(cpu.getMemoryBase() + arg1);
        	cpu.setAccumulator(cpu.getAccumulator() * arg2);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "DIVI"
        INSTRUCTIONS.put(0x13, arg ->
        {
        	if(arg == 0) throw new DivideByZeroException("Cannot divide by 0");
        	else cpu.setAccumulator(cpu.getAccumulator() / arg);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "DIV"
        INSTRUCTIONS.put(0x14, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	if(arg1 == 0) throw new DivideByZeroException("Cannot divide by 0");
        	else cpu.setAccumulator(cpu.getAccumulator() / arg1);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for "DIVN"
        INSTRUCTIONS.put(0x15, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	int arg2 = memory.getData(cpu.getMemoryBase() + arg1);
        	if(arg2 == 0) throw new DivideByZeroException("Cannot divide by 0");
        	else cpu.setAccumulator(cpu.getAccumulator() / arg2);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for NOP"
        INSTRUCTIONS.put(0x0, arg ->
        {
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for LODI"
        INSTRUCTIONS.put(0x1, arg ->
        {
        	cpu.setAccumulator(arg);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for LOD"
        INSTRUCTIONS.put(0x2, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	cpu.setAccumulator(arg1);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for LODN"
        INSTRUCTIONS.put(0x3, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	int arg2 = memory.getData(cpu.getMemoryBase() + arg1);
        	cpu.setAccumulator(arg2);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for STO
        INSTRUCTIONS.put(0x4, arg ->
        {
        	int arg1 = cpu.getAccumulator();
        	memory.setData(arg + cpu.getMemoryBase(), arg1);
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for STON
        INSTRUCTIONS.put(0x5, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	memory.setData(arg1 + cpu.getMemoryBase(), cpu.getAccumulator());
        	cpu.incrementIP();
        });
        //INSTRUCTION_MAP entry for JMPI
        INSTRUCTIONS.put(0x6, arg -> 
        {
        	cpu.setInstructionPointer(cpu.getInstructionPointer() + arg);
        });
        //INSTRUCTION_MAP entry for JUMP
        INSTRUCTIONS.put(0x7, arg ->
        {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	cpu.setInstructionPointer(cpu.getInstructionPointer() + arg1);
        });
        //INSTRUCTION_MAP entry for JMZI
        INSTRUCTIONS.put(0x8, arg ->
        {
        	if(cpu.getAccumulator() == 0)
        	{
        		cpu.setInstructionPointer(cpu.getInstructionPointer() + arg);
        	}
        	else
        	{
        		cpu.incrementIP();
        	}
        });
			//INSTRUCTION_MAP entry for JMPZ
			INSTRUCTIONS.put(0x9, arg ->
			{
				if(cpu.getAccumulator() == 0)
				{
					cpu.setInstructionPointer(cpu.getInstructionPointer()+memory.getData(arg + cpu.getMemoryBase()));
				}
				else
				{
					cpu.incrementIP();
				}
			});
			//INSTRUCTIONS_MAP entry for ANDI
			INSTRUCTIONS.put(0x16, arg ->
			{
				if(cpu.getAccumulator() != 0 && arg != 0)
				{
					cpu.setAccumulator(1);
				}
				else
				{
					cpu.setAccumulator(0);
				}
				cpu.incrementIP();
			});
			//INSTRUCTIONS_MAP entry for AND
			INSTRUCTIONS.put(0x17, arg ->
			{
				int arg1 = memory.getData(cpu.getMemoryBase() + arg);
				if(cpu.getAccumulator() != 0 && arg1 != 0)
				{
					cpu.setAccumulator(1);
				}
				else
				{
					cpu.setAccumulator(0);
				}
				cpu.incrementIP();
			});
			//INSTRUCTIONS_MAP entry for NOT
			INSTRUCTIONS.put(0x18, arg ->
			{
				if(cpu.getAccumulator() != 0)
				{
					cpu.setAccumulator(0);
				}
				else
				{
					cpu.setAccumulator(1);
				}
				cpu.incrementIP();
			});
			//INSTRUCTIONS_MAP entry for CMPL 
			INSTRUCTIONS.put(0x19, arg ->
			{
				int arg1 = memory.getData(cpu.getMemoryBase() + arg);
				if(arg1 < 0)
				{
					cpu.setAccumulator(1);
				}
				else
				{
					cpu.setAccumulator(0);
				}
				cpu.incrementIP();
				cpu.getMemoryBase();
			});
			//INSTRUCTIONS_MAP entry for CMPZ
			INSTRUCTIONS.put(0x1A, arg ->
			{
				if(memory.getData(arg + cpu.getMemoryBase())==0)
				{
        		cpu.setAccumulator(1);
        	}
        	else
        	{
        		cpu.setAccumulator(0);
        	}
        	cpu.incrementIP();
    		cpu.getMemoryBase();
        });
        //INSTRUCTIONS_MAP entry for JMPN 
        INSTRUCTIONS.put(0x1B, arg -> {
        	int target = memory.getData(cpu.getMemoryBase() + arg);
        	cpu.setInstructionPointer(currentJob.getStartcodeIndex()+target);
        });
        //INSTRUCTIONS_MAP entry for HALT
        INSTRUCTIONS.put(0x1F, arg ->
        {
        	callback.halt();
        });
        for(int i = 0; i < jobs.length; i++) 
        {
        	jobs[i] = new Job();
        }
        currentJob = jobs[0];
        jobs[0].setStartcodeIndex(0);
        jobs[0].setStartMemoryIndex(0);
        jobs[1].setStartcodeIndex(Code.CODE_MAX/4);
        jobs[1].setStartMemoryIndex(Memory.DATA_SIZE/2);
        
	}
	public boolean equals(Object obj)
	{
		return memory.equals(obj);
	}
	public int hashCode()
	{
		return memory.hashCode();
	}
	
	public String toString()
	{
		return memory.toString();
	}
	public int getAccumulator()
	{
		return cpu.getAccumulator();
	}
	public int getInstructionPointer()
	{
		return cpu.getInstructionPointer();
	}
	public int getMemoryBase()
	{
		return cpu.getMemoryBase();
	}
	public void setAccumulator(int accumulator)
	{
		cpu.setAccumulator(accumulator);
	}
	public void setInstructionPointer(int instructionPointer)
	{
		cpu.setInstructionPointer(instructionPointer);
	}
	public void setMemoryBase(int memoryBase)
	{
		cpu.setMemoryBase(memoryBase);
	}
	public void incrementIP()
	{
		cpu.incrementIP();
	}
	public Instruction get(Integer x)
	{
		return INSTRUCTIONS.get(x);
	}
	public int[] getData()
	{
		return memory.getData();
	}
	public int getData(int index)
	{
		return memory.getData(index);
	}
	public void setData(int index, int value)
	{
		memory.setData(index, value);
	}
	int getChangedIndex()
	{
		return memory.getChangedIndex();
	}
	public Code getCode()
	{
		return code;
	}
	public void setCode(int i, int op, int arg)
	{	
		code.setCode(i, op, arg);
	}
	public Job getCurrentJob()
	{
		return currentJob;
	}
	public void setJob(int i)
	{
		//System.out.println(currentJob == jobs[0]);
		//System.out.println(this.getInstructionPointer());
		//System.out.println(i);
		if(i != 0 && i != 1)
		{
			throw new IllegalArgumentException("Illegal argument");
		}
		currentJob.setCurrentAcc(this.getAccumulator());
		currentJob.setCurrentIP(this.getInstructionPointer());
		currentJob = jobs[i];
		//System.out.println(currentJob == jobs[1]);
		cpu.setAccumulator(currentJob.getCurrentAcc());
		//System.out.println(currentJob.getCurrentIP());
		cpu.setInstructionPointer(currentJob.getCurrentIP());
		//System.out.println(currentJob.getStartmemoryIndex());
		cpu.setMemoryBase(currentJob.getStartmemoryIndex());
	}
	public States getCurrentState()
	{
		return currentJob.getCurrentState();
	}
	public void setCurrentState(States currentState)
	{
		currentJob.setCurrentState(currentState);
	}
	public void clearJob()
	{
		memory.clear(currentJob.getStartmemoryIndex(), currentJob.getStartmemoryIndex()+Memory.DATA_SIZE/2);
		code.clear(currentJob.getStartcodeIndex(), currentJob.getStartcodeIndex()+currentJob.getCodeSize());
		cpu.setAccumulator(0);
		cpu.setInstructionPointer(currentJob.getStartcodeIndex());
		currentJob.reset();
	}
	public void step()
	{
		try
		{
			int ip = cpu.getInstructionPointer();
			if(ip < currentJob.getStartcodeIndex() || ip >= currentJob.getStartcodeIndex()+currentJob.getCodeSize())
			{
				throw new CodeAccessException("instructionPointer not inbetween startcodeIndex and ending index");
			}
			int opcode = code.getOp(ip);
			int arg = code.getArg(ip);
			get(opcode).execute(arg);
		}
		catch(Exception e)
		{
			callback.halt();
			throw e;
			
		}
	}
}