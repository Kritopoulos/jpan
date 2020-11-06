package com.example.jpan.analysis;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.example.jpan.model.Violation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Component
public class AnalyzerOfJava {


    //private String url;
    private int analyzed_files;
    private int violations_found;
    private int important;
    private int ordinary;
    private int insignificant;
    
    private double overall_Score;
    private String overall_quality;
    private ArrayList<String> analyzed_files_name;


    @Autowired
    private Violation violation;
    private ArrayList<Violation> important_violations;
    private ArrayList<Violation> ordinary_violations;
    private ArrayList<Violation> insignificant_violations;
    private static final int BUFFER_SIZE = 4096;

    public void downLoadZipFileFromGithub(String sUrl) throws MalformedURLException {

        URL url = new URL(sUrl);

        //File where to be downloaded
        File file = new File("D:/user/Desktop/Ptixiaki/FilesFromGit/file.zip");
        try {
            InputStream input = url.openStream();
            if (file.exists()) {
                if (file.isDirectory())
                    throw new IOException("File '" + file + "' is a directory");

                if (!file.canWrite())
                    throw new IOException("File '" + file + "' cannot be written");
            } else {
                File parent = file.getParentFile();
                if ((parent != null) && (!parent.exists()) && (!parent.mkdirs())) {
                    throw new IOException("File '" + file + "' could not be created");
                }
            }

            FileOutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }

            input.close();
            output.close();
            unzipFile(file.toString());
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }

    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public void unzipFile(String source) throws IOException {

        String destDirectory = ("D:/user/Desktop/Ptixiaki/FilesFromGit");

        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(source));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        getQuality(destDirectory);
    }

    private List<DataSource> determineFiles(String basePath) throws IOException {
        Path dirPath = FileSystems.getDefault().getPath(basePath);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");

        List<DataSource> files = new ArrayList<>();
        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (matcher.matches(path.getFileName())) {
                    String[] temp = path.toString().split(Pattern.quote(File.separator));
                    analyzed_files_name.add(temp[temp.length - 1]);
                    files.add(new FileDataSource(path.toFile()));
                }
                analyzed_files = analyzed_files_name.size();
                return super.visitFile(path, attrs);
            }
        });
        return files;
    }

    public void getQuality(String source) throws IOException {

        overall_Score = 0;
        analyzed_files_name = new ArrayList<>();
        ordinary_violations = new ArrayList<>();
        important_violations = new ArrayList<>();
        insignificant_violations = new ArrayList<>();

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setMinimumPriority(RulePriority.MEDIUM);
        configuration.setRuleSets("rulesets/java/quickstart.xml");
        configuration.prependClasspath("/home/workspace/target/classes");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.createFactory(configuration);

        List<DataSource> files = determineFiles(source);
        Writer rendererOutput = new StringWriter();
        Renderer renderer = createRenderer(rendererOutput);
        renderer.start();
        RuleContext ctx = new RuleContext();
        //- ctx.getReport().addListener(createReportListener()); // alternative way to collect violations
        try {
            PMD.processFiles(configuration, ruleSetFactory, files, ctx,
                    Collections.singletonList(renderer));
        } finally {
            ClassLoader auxiliaryClassLoader = configuration.getClassLoader();
            if (auxiliaryClassLoader instanceof ClasspathClassLoader) {
                //noinspection deprecation
                ((ClasspathClassLoader) auxiliaryClassLoader).close();
            }
        }


        renderer.end();
        renderer.flush();
        writeViolations(rendererOutput.toString());
        for(DataSource f : files){
            f.close();
        }
        deleteDownLoadedFiles(source);
    }

    private void deleteDownLoadedFiles(String source) throws IOException {
            FileUtils.deleteDirectory(new File(source));
    }

    private static Renderer createRenderer(Writer writer) {
        XMLRenderer xml = new XMLRenderer("UTF-8");
        xml.setWriter(writer);
        return xml;
    }

    private void writeViolations(String renderer) {
        try {
            DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = newDocumentBuilder.parse(new ByteArrayInputStream(renderer.getBytes()));
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("file");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Element eElement = (Element) nNode;

                NodeList vList = doc.getElementsByTagName("violation");

                for (int i = 0; i < vList.getLength(); i++) {

                    Node vnode = vList.item(i);

                    if (vnode.getNodeType() == vnode.ELEMENT_NODE) {

                        Element vElement = (Element) vnode;

                        violation = new Violation();
                        overall_Score += Integer.parseInt(vElement.getAttribute("priority"));
                        System.out.println(vElement.getAttribute("priority"));
                        violation.setViolation(vElement.getAttribute("priority"), vElement.getTextContent().replace("\n", ""),
                                vElement.getAttribute("beginline"), vElement.getAttribute("endline"), vElement.getAttribute("begincolumn"),
                                vElement.getAttribute("endcolumn"), vElement.getAttribute("rule"), vElement.getAttribute("ruleset"),
                                vElement.getAttribute("class"), vElement.getAttribute("method"));
//                        violation = new Violation(vElement.getAttribute("priority"),vElement.getTextContent().replace("\n",""),
//                                vElement.getAttribute("beginline"),vElement.getAttribute("endline"),vElement.getAttribute("begincolumn"),
//                                vElement.getAttribute("endcolumn"),vElement.getAttribute("rule"),vElement.getAttribute("ruleset"),
//                                vElement.getAttribute("class"), vElement.getAttribute("method"));

//                        violation.setPriority(vElement.getAttribute("priority"));
//                        violation.setDescription(vElement.getTextContent().replace("\n",""));
//                        violation.setBegin_line(vElement.getAttribute("beginline"));
//                        violation.setBegin_column(vElement.getAttribute("begincolumn"));
//                        violation.setEnd_line(vElement.getAttribute("endline"));
//                        violation.setEnd_column(vElement.getAttribute("endcolumn"));
//                        violation.setRule(vElement.getAttribute("rule"));
//                        violation.setRule_set(vElement.getAttribute("ruleset"));
//                        violation.setMethod(vElement.getAttribute("method"));
//                        violation.seteClass(vElement.getAttribute("class"))
                        if (Integer.parseInt(vElement.getAttribute("priority")) <= 2) {
                            important_violations.add(violation);
                        } else if (Integer.parseInt(vElement.getAttribute("priority")) <= 3) {
                            ordinary_violations.add(violation);
                        } else if (Integer.parseInt(vElement.getAttribute("priority")) <= 5) {
                            insignificant_violations.add(violation);
                        }
                    }
                }
            }

            important = important_violations.size();
            ordinary = ordinary_violations.size();
            insignificant = insignificant_violations.size();

            violations_found = important + ordinary +  insignificant;
            overall_Score = overall_Score / violations_found;

            if (overall_Score < 2.0) {
                overall_quality = "code needs do be changed";
            } else if (overall_Score < 2.5) {
                overall_quality = "keep coding you can do better";
            } else if (overall_Score < 3) {
                overall_quality = "good but not good enough. Keep coding";
            } else if (overall_Score < 4) {
                overall_quality = "overall code is good but you can do better";
            } else if (overall_Score <= 5) {
                overall_quality = "a few more steps to be perfect";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Violation> getImportant_violations() {
        return important_violations;
    }

    public ArrayList<Violation> getInsignificant_violations() {
        return insignificant_violations;
    }

    public void setImportant_violations(ArrayList<Violation> important_violations) {
        this.important_violations = important_violations;
    }

    public void setInsignificant_violations(ArrayList<Violation> insignificant_violations) {
        this.insignificant_violations = insignificant_violations;
    }

    public ArrayList<Violation> getOrdinary_violations() {
        return ordinary_violations;
    }

    public void setOrdinary_violations(ArrayList<Violation> ordinary_violations) {
        this.ordinary_violations = ordinary_violations;
    }

    public int getAnalyzed_files() {
        return analyzed_files;
    }

    public int getViolations_found() {
        return violations_found;
    }

    public ArrayList<String> getAnalyzed_files_name() {
        return analyzed_files_name;
    }

    public void setAnalyzed_files_name(ArrayList<String> analyzed_files_name) {
        this.analyzed_files_name = analyzed_files_name;
    }

    public double getOverall_Score() {
        return overall_Score;
    }

    public String getOverall_quality() {
        return overall_quality;
    }

    public int getImportant() {
        return important;
    }

    public int getOrdinary() {
        return ordinary;
    }

    public int getInsignificant() {
        return insignificant;
    }

}
