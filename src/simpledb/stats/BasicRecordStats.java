package simpledb.stats;

//import java.util.List;

public class BasicRecordStats {
	
	private int readRecord;
	private int writtenRecord;
	private int readFieldsRecord;
	private int writtenFieldsRecord;
	
	public BasicRecordStats(){
		this.readRecord=0;
		this.writtenRecord=0;
		this.readFieldsRecord=0;
		this.writtenFieldsRecord=0;
	}
	
	public int getReadRecord() {
		return this.readRecord;
	}
	
	public int getWrittenRecord() {
		return this.writtenRecord;
	}
	
	public int getReadFieldsRecord() {
		return this.readFieldsRecord;
	}

	public int getWrittenFieldsRecord() {
		return this.writtenFieldsRecord;
	}
	
	public void incWrittenFieldsRecord() {
		this.writtenFieldsRecord++;
	}
	public void incReadFieldsRecord() {
		this.readFieldsRecord++;
	}
	public void incWrittenRecord() {
		this.writtenRecord++;
	}
	public void incReadRecord() {
		this.readRecord++;
	}
	
	public String toString(){
		String rb = "\n - Letture per il record   : ".concat(Integer.toString(this.getReadRecord()))+"\n";
		rb=rb.concat("  -> Numero di campi letti : "+this.getReadFieldsRecord()+"\n");
		String wb = "\n - Scritture per il record : ".concat(Integer.toString(this.getWrittenRecord()))+"\n";
		wb=wb.concat("  -> Numero   campi scritti: "+this.getWrittenFieldsRecord()+"\n");
		return rb+wb;
	}
	
}
