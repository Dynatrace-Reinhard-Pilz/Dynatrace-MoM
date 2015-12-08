package com.dynatrace.jsp.taglibs.mom;

import java.util.ArrayList;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTag;
import javax.servlet.jsp.tagext.IterationTag;

import org.apache.taglibs.standard.tag.common.core.ForEachSupport;

/**
 * <p>A handler for &lt;forEach&gt; that supports rtexprvalue-based
 * attributes.</p>
 *
 * @author Shawn Bayern
 */

public class ForEachTag
    extends ForEachSupport
    implements LoopTag, IterationTag
{
	private static final long serialVersionUID = 1L;

    //*********************************************************************
    // Accessor methods


	// for tag attribute
    public void setBegin(int begin) throws JspTagException {
        this.beginSpecified = true;
        this.begin = begin;
        validateBegin();
    }

    // for tag attribute
    public void setEnd(int end) throws JspTagException {
        this.endSpecified = true;
        this.end = end;
        validateEnd();
    }

    // for tag attribute
    public void setStep(int step) throws JspTagException {
        this.stepSpecified = true;
        this.step = step;
        validateStep();
    }

    public void setItems(Object o) throws JspTagException {
	// for null items, simulate an empty list
	if (o == null)
	    rawItems = new ArrayList<Object>();
        else
	    rawItems = o;
    }
}
