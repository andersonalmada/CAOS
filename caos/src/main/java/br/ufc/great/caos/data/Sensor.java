package br.ufc.great.caos.data;

public class Sensor {

	public String type;
	public Object value;
	public long timestamp;
	
	public Sensor(String type, Object value) {
		super();
		this.type = type;
		this.value = value;
		this.timestamp = System.currentTimeMillis();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Sensor [type=" + type + ", value=" + value + ", timestamp=" + timestamp + "]";
	}		
}
