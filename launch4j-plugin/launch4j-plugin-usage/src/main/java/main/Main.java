package main;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class Main {
	public static void main(String[] args) throws Exception {
		System.out.println("Commons Lang - " + StringUtils.INDEX_NOT_FOUND);
		System.out.println("Commons IO - " + IOUtils.DIR_SEPARATOR_UNIX);
	}
}
