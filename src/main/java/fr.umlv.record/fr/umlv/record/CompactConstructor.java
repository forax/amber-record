package fr.umlv.record;

record CompactConstructor(String s) {
	public CompactConstructor {
		System.out.println("then " + s);
	}
	
	public static void main(String[] args) {
		var compact = new CompactConstructor("foo");
		System.out.println(compact);
	}
}
