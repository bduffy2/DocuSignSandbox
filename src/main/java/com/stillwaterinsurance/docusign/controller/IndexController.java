package com.stillwaterinsurance.docusign.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/index")
public class IndexController {
	
	@RequestMapping(value={"", "/"}, method=RequestMethod.GET)
	public String index(final ModelAndView mav, final HttpSession session) {
		return "/index";
	}
	
}
