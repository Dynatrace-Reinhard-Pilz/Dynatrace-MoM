package com.dynatrace.fixpacks;

import java.io.File;
import java.io.FileInputStream;

import com.dynatrace.xml.XMLUtil;

public class FixPackInfoTest {

	public static void main(String[] args) throws Exception {
		File infoFile = new File("C:\\Users\\cmarxp0\\.dynaTrace\\webapp-mom\\fixpacks\\dynaTrace-6.1.0.8191\\fixpackinfo.xml");
		FileInputStream in = new FileInputStream(infoFile);
		FixPackInfo fixPackInfo = XMLUtil.<FixPackInfo>deserialize(in, FixPackInfo.class);
		XMLUtil.serialize(fixPackInfo, System.out);
	}

}
