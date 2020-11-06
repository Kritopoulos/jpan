package com.example.jpan.controller;

import com.example.jpan.analysis.AnalyzerOfJava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.HashMap;

@RestController
public class Controller {


    @Autowired
    AnalyzerOfJava analyzerOfJava;

    @GetMapping("/code_analyze")
    //@ResponseBody
    public HashMap<String, AnalyzerOfJava> getQuality(@RequestParam(name = "url") String url) throws IOException {

        //analyzerOfJava.setUrl(url);
        //analyzerOfJava.setUrl("D:/user/Desktop/FOL");
        analyzerOfJava.downLoadZipFileFromGithub(url);

        HashMap<String, AnalyzerOfJava> map = new HashMap<>();
        map.put("Response", analyzerOfJava);
        return map;

    }
}

//    @RequestMapping("/")
//    public ModelAndView home()
//    {
//        ModelAndView mv = new ModelAndView("home");
//        return mv;
//
//    }
//
////    @RequestMapping("/addAlien")
////    public ModelAndView addAlien(Alien alien)
////    {
////        ModelAndView mv = new ModelAndView("home");
////        repo.save(alien);
////        return mv;
////    }
//    @PostMapping("/alien")
//    public Alien addAlien(@RequestBody Alien alien)  {
//        repo.save(alien);
//        return alien;
//    }
////    @RequestMapping("/getAlien")
////    public ModelAndView getAlien(@RequestParam int aid)
////    {
////        ModelAndView mv = new ModelAndView("showAlien");
////
////        System.out.println(repo.findByTech("java"));
////        System.out.println(repo.findByAidGreaterThan(102));
////        System.out.println(repo.findByTechSorted("java"));
////        Alien alien = repo.findById(aid).orElse(new Alien());
////        mv.addObject(alien);
////
////        return mv;
////    }
//
//
//
//
//    @GetMapping("/aliens")
//    //@ResponseBody
//    public List<Alien> getAliens()
//    {
//       return repo.findAll();
//    }
//
//    @GetMapping("/alien/{aid}")
//    //@ResponseBody
//    public Optional<Alien> getAlien(@PathVariable int aid)
//    {
//        return repo.findById(aid);
//    }
//
//    @DeleteMapping("/alien/{aid}")
//    //@ResponseBody
//    public String deleteAlien(@PathVariable int aid)
//    {
//        Alien a = repo.getOne(aid);
//        repo.delete(a);
//        return "deleted";
//
//    }
//
//    @PutMapping(path="alien",consumes = {"application/json"})
//    public Alien saveOrUpdateAlien(@RequestBody Alien alien)
//    {
//        repo.save(alien);
//        return alien;
//    }

