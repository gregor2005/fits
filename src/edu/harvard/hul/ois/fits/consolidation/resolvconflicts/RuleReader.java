package edu.harvard.hul.ois.fits.consolidation.resolvconflicts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.harvard.hul.ois.fits.Fits;

public class RuleReader {
	private static RuleReader reader = null;
	private HashMap<String, List<Rule>> output = null;
	private static final String RULES_CONFIG = "rules.conf";

	private RuleReader() {
		//
	}

	public static RuleReader getInstance() {
		if (reader == null) {
			reader = new RuleReader();
		}
		return reader;
	}

	public HashMap<String, List<Rule>> getRules() {
		if (output == null) {
			// output = mookupRules();
			output = readRules();
		}
		return output;
	}

	private HashMap<String, List<Rule>> readRules() {
		// TODO throw execption on error
		String filename = Fits.FITS_HOME + RULES_CONFIG;
		System.out.println("-------------read line: " + filename);
		BufferedReader br = null;
		List<String> lines = new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();

			while (line != null) {
				lines.add(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("-------all lines where read");
		// syntax: rulenname,mimetype,version,conflicted field,prevent tool,tool
		// version
		// example: rule1,application/pdf,1.4,creatingApplicationName,Jhove,1.5
		Pattern patternComment = Pattern.compile(".*#.*");
		Pattern patternRule = Pattern.compile("(.*),(.*),(.*),(.*),(.*),(.*)");
		HashMap<String, List<Rule>> outputRules = new HashMap<>();
		for (String line : lines) {
			Matcher matcherComment = patternComment.matcher(line);
			Matcher matcherRule = patternRule.matcher(line);
			if (!matcherComment.find()) {
				if (matcherRule.find()) {
					if (matcherRule.groupCount() == 6) {
						Rule rule = new Rule();
						rule.setMimetypeNeeded(matcherRule.group(2));
						rule.setFileVersionNeeded(matcherRule.group(3));
						rule.setField(matcherRule.group(4));
						rule.setToolName(matcherRule.group(5));
						rule.setToolVersion(matcherRule.group(6));
						if (outputRules.get(rule.getMimetypeNeeded()) == null) {
							List<Rule> rules = new ArrayList<>();
							rules.add(rule);
							outputRules.put(rule.getMimetypeNeeded(), rules);
						} else {
							outputRules.get(rule.getMimetypeNeeded()).add(rule);
						}
						System.out.println("------stored rule: "+rule.toString());
					} else {
						System.out.println("!!! wrong rule, not 6 tokens: "
								+ line);
					}
				}
			} else {
				System.out.println("-------comment found in rule: " + line);
			}
		}
		return outputRules;
	}

	private HashMap<String, List<Rule>> mookupRules() {
		HashMap<String, List<Rule>> outputMookup = new HashMap<>();

		List<Rule> pdf = new ArrayList<>();
		Rule rule1 = new Rule();
		rule1.setMimetypeNeeded("application/pdf");
		rule1.setFileVersionNeeded("1.4");
		rule1.setField("creatingApplicationName");
		rule1.setToolName("Jhove");
		rule1.setToolVersion("1.5");
		pdf.add(rule1);
		outputMookup.put(rule1.getMimetypeNeeded(), pdf);

		List<Rule> jpeg = new ArrayList<>();
		Rule rule2 = new Rule();
		rule2.setMimetypeNeeded("image/jpeg");
		rule2.setFileVersionNeeded("2.2");
		rule2.setField("creatingApplicationName");
		rule2.setToolName("NLNZ Metadata Extractor");
		rule2.setToolVersion("3.4GA");
		jpeg.add(rule2);
		outputMookup.put(rule2.getMimetypeNeeded(), jpeg);

		Rule rule3 = new Rule();
		rule3.setMimetypeNeeded("image/jpeg");
		rule3.setFileVersionNeeded("2.2");
		rule3.setField("creatingApplicationName");
		rule3.setToolName("Jhove");
		rule3.setToolVersion("1.5");
		jpeg.add(rule3);
		outputMookup.put(rule3.getMimetypeNeeded(), jpeg);

		return outputMookup;
	}

}
