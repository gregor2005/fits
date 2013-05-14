package edu.harvard.hul.ois.fits.consolidation.resolvconflicts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

public class ResolvConflicts {
	
	// TODO logging einbauen

	private static List<Element> fitsElements = new ArrayList<Element>();

	public static List<Element> prepareElementList(List<Element> fitsElements2,
			String mimetype, String fileVersion) {
		fitsElements = new ArrayList<Element>();
		// if (fitsElements2.size() > 1) {
		System.out.println("+++++++ conflicts found = " + fitsElements2.size());
		if (fitsElements2.size() > 2) {
			System.out.println("+++++++ conflicts found more than 2: "
					+ fitsElements2.get(0).getValue());
			if (!fitsElements2.get(0).getValue()
					.equals(fitsElements2.get(1).getValue())
					&& !fitsElements2.get(1).getValue()
							.equals(fitsElements2.get(2).getValue())
					&& !fitsElements2.get(0).getValue()
							.equals(fitsElements2.get(2).getValue())) {
				System.out.println("++++++++found 3 different values");
				// System.exit(1);
			}
			for (Element e : fitsElements2) {
				System.out.println("+++++++ element: " + e.getValue());
			}
			// System.exit(1);
		}
		// } -----------------------------------------------------------

		if (fitsElements2 == null) {
			fitsElements = checkForConflictsAndMark(fitsElements2);
			return fitsElements;
		}
		RuleReader rulereader = RuleReader.getInstance();
		HashMap<String, List<Rule>> rules = rulereader.getRules();

		if (rules == null) {
			System.out.println("------------rules are null");
			fitsElements = checkForConflictsAndMark(fitsElements2);
			return fitsElements;
		}

		if (!rules.keySet().contains(mimetype)) {
			System.out
					.println("------------mimetype and version numer not found");
			fitsElements = checkForConflictsAndMark(fitsElements2);
			return fitsElements;
		} else {
			System.out.println("------------found mimetype");
		}
		List<Rule> mimetypeRules = rules.get(mimetype);
		if (mimetypeRules.size() < 1) {
			System.out.println("------------no rules in mimetype found");
			fitsElements = checkForConflictsAndMark(fitsElements2);
			return fitsElements;
		}
		System.out.println("-----------start");
		if (fitsElements2.size() > 1) {
			boolean doRemove = false;
			for (Rule rule : mimetypeRules) {
				if (fitsElements.size() > 0) {
					doRemove = true;
				}
				if (rule.getFileVersionNeeded().equals(fileVersion)) {
					System.out.println("---------------iterate list size: "
							+ fitsElements.size());
					for (Element e : fitsElements2) {
						System.out.println("-----process element: "
								+ e.getName());
						boolean toolFound = false;
						boolean toolVersionFound = false;

						if (e.getName().equals(rule.getField())) {
							System.out.println("-----field found: "
									+ e.getName());
							for (Object a : e.getAttributes()) {
								if (a instanceof Attribute) {
									Attribute attribute = (Attribute) a;
									System.out.println("------------name: "
											+ attribute.getName());
									System.out.println("------------value: "
											+ attribute.getValue());
									if (attribute.getValue().equals(
											rule.getToolName())) {
										System.out
												.println("-----------tool found");
										toolFound = true;
									}
									if (attribute.getValue().equals(
											rule.getToolVersion())) {
										System.out
												.println("-----------tool version found");
										toolVersionFound = true;
									}
								}
							}
						}
						if (toolFound && toolVersionFound) {
							// ommit output
							System.out.println("-----------ommit output");
							if (doRemove) {
								fitsElements.remove(e);
							}
						} else {
							if (!doRemove) {
								System.out
										.println("-----------add element to output list");
								fitsElements.add(e);
							}
						}
					}
				}
			}
		} else {
			fitsElements = checkForConflictsAndMark(fitsElements);
			return fitsElements;
		}
		if (fitsElements.size() > 1) {
			System.out.println("+++++++ conflicts still exists");
		}
		fitsElements = checkForConflictsAndMark(fitsElements);
		System.out.println("-----------end");
		return fitsElements;
	}

	private static List<Element> checkForConflictsAndMark(
			List<Element> fitsElements2) {
		System.out.println("-----------checkForConflictsAndMark start");
		if (fitsElements2.size() > 1) {
			HashMap<String, String> values = new HashMap<>();
			boolean stillConflicted = false;
			for (Element e : fitsElements2) {
				values.put(e.getValue(), e.getValue());
				if (values.keySet().size() > 1) {
					// found more than different values
					stillConflicted = true;
					break;
				}
			}
			if (stillConflicted) {
				for (Element e : fitsElements2) {
					e.setAttribute("status", "CONFLICT");
				}
			}
			return fitsElements2;
		} else {
			return fitsElements2;
		}
	}

}
