package edu.harvard.hul.ois.fits.consolidation.resolvconflicts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

public class ResolvConflicts {

	private static List<Element> fitsElements = new ArrayList<Element>();
	private static Logger log = Logger.getLogger(ResolvConflicts.class);

	public static List<Element> prepareElementList(List<Element> fitsElements2,
			String mimetype, String fileVersion) {
		fitsElements = new ArrayList<Element>();
		if (fitsElements2 == null) {
			fitsElements = checkForConflictsAndMark(fitsElements2);
			return fitsElements;
		}
		log.debug("conflicts found = " + fitsElements2.size());
		if (fitsElements2.size() > 2) {
			log.debug("conflicts found more than 2: "
					+ fitsElements2.get(0).getValue());
			if (!fitsElements2.get(0).getValue()
					.equals(fitsElements2.get(1).getValue())
					&& !fitsElements2.get(1).getValue()
							.equals(fitsElements2.get(2).getValue())
					&& !fitsElements2.get(0).getValue()
							.equals(fitsElements2.get(2).getValue())) {
				log.debug("found 3 different values");
			}
			for (Element e : fitsElements2) {
				log.debug("element: " + e.getValue());
			}
		}
		// } -----------------------------------------------------------

		RuleReader rulereader = RuleReader.getInstance();
		HashMap<String, List<Rule>> rules = rulereader.getRules();

		if (rules == null) {
			log.error("rules are null");
			fitsElements = checkForConflictsAndMark(fitsElements2);
			return fitsElements;
		}

		if (!rules.keySet().contains(mimetype)) {
			log.debug("mimetype and version numer not found");
			fitsElements = checkForConflictsAndMark(fitsElements2);
			return fitsElements;
		} else {
			log.debug("found mimetype");
		}
		List<Rule> mimetypeRules = rules.get(mimetype);
		if (mimetypeRules.size() < 1) {
			log.debug("no rules in mimetype found");
			fitsElements = checkForConflictsAndMark(fitsElements2);
			return fitsElements;
		}
		if (fitsElements2.size() > 1) {
			boolean doRemove = false;
			for (Rule rule : mimetypeRules) {
				if (fitsElements.size() > 0) {
					doRemove = true;
				}
				if (rule.getFileVersionNeeded().equals(fileVersion)) {
					log.debug("iterate list size: " + fitsElements.size());
					for (Element e : fitsElements2) {
						log.debug("process element: " + e.getName());
						boolean toolFound = false;
						boolean toolVersionFound = false;

						if (e.getName().equals(rule.getField())) {
							log.debug("field found: " + e.getName());
							for (Object a : e.getAttributes()) {
								if (a instanceof Attribute) {
									Attribute attribute = (Attribute) a;
									log.debug("name: " + attribute.getName());
									log.debug("value: " + attribute.getValue());
									if (attribute.getValue().equals(
											rule.getToolName())) {
										log.debug("tool found");
										toolFound = true;
									}
									if (attribute.getValue().equals(
											rule.getToolVersion())) {
										log.debug("tool version found");
										toolVersionFound = true;
									}
								}
							}
						}
						if (toolFound && toolVersionFound) {
							// ommit output
							log.info("ommit output");
							if (doRemove) {
								fitsElements.remove(e);
							}
						} else {
							if (!doRemove) {
								log.debug("add element to output list");
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
			log.debug("conflicts still exists");
		}
		fitsElements = checkForConflictsAndMark(fitsElements);
		return fitsElements;
	}

	private static List<Element> checkForConflictsAndMark(
			List<Element> fitsElements2) {
		log.info("checkForConflictsAndMark start");
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
