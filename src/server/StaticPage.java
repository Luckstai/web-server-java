package server;

import java.io.File;
import java.util.List;

public class StaticPage {

	private String name;
	private String basePath;
	private String extension;
	private List<StaticPage> listPages;
	
	public StaticPage(String name, String basePath) {
		this.name = name;
		this.basePath = basePath;
		this.extension = extension;
	}
	
	public StaticPage() {
		
	}
	
	public StaticPage(File file) {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	public void makeListStaticPages(String filePath) {
		File directory = new File(filePath);
		File files[] = directory.listFiles();
		int i = 0;
		for (int j = files.length; i < j; i++) {
			File arquivo = files[i];
			System.out.println("PRINTANDO TESTE DE LER DIRETÓRIO");
			System.out.println("ABSOLUTE PATH " + arquivo.getAbsolutePath());
			System.out.println("IS DIRECTORY " + arquivo.isDirectory());
			System.out.println("IS FILE " + arquivo.isFile());
			System.out.println("exists " + arquivo.exists());
			System.out.println(arquivo.getName());
//			list.add(new StaticPage(arquivo.getName(), arquivo.getAbsolutePath()));
		}
	}

	@Override
	public String toString() {
		return "StaticPage [name=" + name + ", basePath=" + basePath + ", extension=" + extension + "]";
	}
	
	
	
}
