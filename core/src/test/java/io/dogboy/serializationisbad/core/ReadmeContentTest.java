package io.dogboy.serializationisbad.core;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests that verify the README.md contains the "Uses" section added in this PR,
 * including the heading "## Uses: ugba908@gmail.com" and correct placement
 * relative to adjacent sections.
 */
public class ReadmeContentTest {

    private List<String> readmeLines;
    private String readmeContent;

    @Before
    public void loadReadme() throws IOException {
        // Locate README.md relative to the project root (two levels above core/)
        File readmeFile = new File(
            getClass().getProtectionDomain().getCodeSource().getLocation().getFile()
        );
        // Walk up from build/classes/... to the module root, then to the project root
        File projectRoot = readmeFile.getParentFile(); // java
        while (projectRoot != null && !new File(projectRoot, "README.md").exists()) {
            projectRoot = projectRoot.getParentFile();
        }

        if (projectRoot == null || !new File(projectRoot, "README.md").exists()) {
            // Fallback: try the working directory
            projectRoot = new File(System.getProperty("user.dir"));
            while (projectRoot != null && !new File(projectRoot, "README.md").exists()) {
                projectRoot = projectRoot.getParentFile();
            }
        }

        assertTrue("README.md must be findable from project root", projectRoot != null);
        File readme = new File(projectRoot, "README.md");
        assertTrue("README.md must exist", readme.exists());

        readmeLines = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(readme));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                readmeLines.add(line);
                sb.append(line).append('\n');
            }
        } finally {
            reader.close();
        }
        readmeContent = sb.toString();
    }

    /** The exact heading added by this PR must appear in the README. */
    @Test
    public void testUsesHeadingExists() {
        assertTrue(
            "README must contain the '## Uses: ugba908@gmail.com' heading",
            readmeContent.contains("## Uses: ugba908@gmail.com")
        );
    }

    /** The email address ugba908@gmail.com must be present in the README. */
    @Test
    public void testEmailAddressPresent() {
        assertTrue(
            "README must contain the email address 'ugba908@gmail.com'",
            readmeContent.contains("ugba908@gmail.com")
        );
    }

    /** The heading must use the correct level-2 markdown prefix "## ". */
    @Test
    public void testUsesHeadingIsLevel2() {
        boolean found = false;
        for (String line : readmeLines) {
            if (line.startsWith("## Uses:")) {
                found = true;
                assertTrue(
                    "Uses heading must start with '## ' (level-2 markdown)",
                    line.startsWith("## ")
                );
                assertFalse(
                    "Uses heading must not be level-1 (single '#')",
                    line.startsWith("# Uses:")
                );
                break;
            }
        }
        assertTrue("A line beginning with '## Uses:' must exist", found);
    }

    /** The Uses section must appear before the Credits section. */
    @Test
    public void testUsesSectionBeforeCredits() {
        int usesIndex = -1;
        int creditsIndex = -1;
        for (int i = 0; i < readmeLines.size(); i++) {
            String line = readmeLines.get(i);
            if (line.startsWith("## Uses:") && usesIndex == -1) {
                usesIndex = i;
            }
            if (line.startsWith("## Credits") && creditsIndex == -1) {
                creditsIndex = i;
            }
        }
        assertNotEquals("'## Uses:' heading must be present", -1, usesIndex);
        assertNotEquals("'## Credits' heading must be present", -1, creditsIndex);
        assertTrue(
            "The Uses section must appear before the Credits section",
            usesIndex < creditsIndex
        );
    }

    /** The Uses section must appear after the Technical Approach section. */
    @Test
    public void testUsesSectionAfterTechnicalApproach() {
        int technicalIndex = -1;
        int usesIndex = -1;
        for (int i = 0; i < readmeLines.size(); i++) {
            String line = readmeLines.get(i);
            if (line.startsWith("## Technical Approach") && technicalIndex == -1) {
                technicalIndex = i;
            }
            if (line.startsWith("## Uses:") && usesIndex == -1) {
                usesIndex = i;
            }
        }
        assertNotEquals("'## Technical Approach' heading must be present", -1, technicalIndex);
        assertNotEquals("'## Uses:' heading must be present", -1, usesIndex);
        assertTrue(
            "The Uses section must appear after the Technical Approach section",
            usesIndex > technicalIndex
        );
    }

    /** Regression: the heading must not have been misspelled or use a different address. */
    @Test
    public void testUsesHeadingDoesNotContainWrongEmail() {
        for (String line : readmeLines) {
            if (line.startsWith("## Uses:")) {
                assertFalse(
                    "Uses heading must not reference an incorrect placeholder email",
                    line.contains("example@example.com")
                );
                assertFalse(
                    "Uses heading must not be empty after the colon",
                    line.trim().equals("## Uses:")
                );
                break;
            }
        }
    }

    /** Boundary: the email in the heading must follow a basic addr@domain.tld format. */
    @Test
    public void testEmailFormatInHeading() {
        for (String line : readmeLines) {
            if (line.startsWith("## Uses:")) {
                String afterColon = line.substring(line.indexOf(':') + 1).trim();
                assertTrue(
                    "The value after '## Uses:' must contain '@'",
                    afterColon.contains("@")
                );
                assertTrue(
                    "The value after '## Uses:' must contain a '.' after '@'",
                    afterColon.indexOf('.') > afterColon.indexOf('@')
                );
                break;
            }
        }
    }

    /** The README must contain exactly one Uses heading (no duplicates). */
    @Test
    public void testExactlyOneUsesHeading() {
        int count = 0;
        for (String line : readmeLines) {
            if (line.startsWith("## Uses:")) {
                count++;
            }
        }
        assertTrue("There must be exactly one '## Uses:' heading in the README", count == 1);
    }
}
