/**
 *
 * Copyright 1997-2005 Unidata Program Center/University Corporation for
 * Atmospheric Research, P.O. Box 3000, Boulder, CO 80307,
 * support@unidata.ucar.edu.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package ucar.unidata.repository;


import org.w3c.dom.*;


import ucar.unidata.sql.SqlUtil;
import ucar.unidata.util.DateUtil;
import ucar.unidata.util.HtmlUtil;
import ucar.unidata.util.IOUtil;
import ucar.unidata.util.Misc;


import ucar.unidata.util.StringUtil;
import ucar.unidata.util.TwoFacedObject;
import ucar.unidata.xml.XmlUtil;


import java.sql.Statement;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 *
 *
 * @author IDV Development Team
 * @version $Revision: 1.3 $
 */
public class ThreddsMetadataHandler extends MetadataHandler {

    /** _more_ */
    public static final String ATTR_NAME = "name";

    /** _more_ */
    public static final String ATTR_ROLE = "role";

    /** _more_ */
    public static final String ATTR_EMAIL = "email";

    /** _more_ */
    public static final String ATTR_URL = "url";

    /** _more_ */
    public static final String ATTR_VOCABULARY = "vocabulary";

    /** _more_ */
    public static final String ATTR_VALUE = "value";



    /** _more_ */
    public static final Metadata.Type TYPE_CREATOR =
        new Metadata.Type("thredds.creator", "Creator");

    /** _more_ */
    public static final Metadata.Type TYPE_LINK =
        new Metadata.Type("thredds.link", "Link");

    /** _more_ */
    public static final Metadata.Type TYPE_DATAFORMAT =
        new Metadata.Type("thredds.dataFormat", "Data Format");

    /** _more_ */
    public static final Metadata.Type TYPE_DATATYPE =
        new Metadata.Type("thredds.dataType", "Data Type");

    /** _more_ */
    public static final Metadata.Type TYPE_AUTHORITY =
        new Metadata.Type("thredds.authority", "Authority");

    /** _more_ */
    public static final Metadata.Type TYPE_VARIABLES =
        new Metadata.Type("thredds.variables", "Variables");

    /** _more_ */
    public static final Metadata.Type TYPE_PUBLISHER =
        new Metadata.Type("thredds.publisher", "Publisher");

    /** _more_ */
    public static final Metadata.Type TYPE_PROJECT =
        new Metadata.Type("thredds.project", "Project");

    /** _more_ */
    public static final Metadata.Type TYPE_KEYWORD =
        new Metadata.Type("thredds.keyword", "Keyword");

    /** _more_ */
    public static final Metadata.Type TYPE_CONTRIBUTOR =
        new Metadata.Type("thredds.contributor", "Contributor");

    /** _more_ */
    public static final Metadata.Type TYPE_PROPERTY =
        new Metadata.Type("thredds.property", "Property");

    /** _more_ */
    public static final Metadata.Type TYPE_DOCUMENTATION =
        new Metadata.Type("thredds.documentation", "Documentation");

    /** _more_ */
    public static final Metadata.Type TYPE_ICON =
        new Metadata.Type("thredds.icon", "Icon");


    /**
     * _more_
     *
     * @param repository _more_
     * @param node _more_
     * @throws Exception _more_
     */
    public ThreddsMetadataHandler(Repository repository, Element node)
            throws Exception {
        super(repository, node);
        addType(TYPE_DOCUMENTATION);
        addType(TYPE_PROPERTY);
        addType(TYPE_LINK);
        addType(TYPE_KEYWORD);
        addType(TYPE_ICON);
        addType(TYPE_PUBLISHER);
        addType(TYPE_CREATOR);
        addType(TYPE_CONTRIBUTOR);
        addType(TYPE_PROJECT);
        addType(TYPE_AUTHORITY);
        addType(TYPE_DATATYPE);
        addType(TYPE_DATAFORMAT);
        addType(TYPE_VARIABLES);
    }




    /**
     * _more_
     *
     * @param type _more_
     *
     * @return _more_
     */
    private String getTag(Metadata.Type type) {
        int idx = type.getType().indexOf(".");
        if (idx < 0) {
            return type.getType();
        }
        return type.getType().substring(idx+1);
    }

    /**
     * _more_
     *
     * @return _more_
     */
    protected String getHandlerGroupName() {
        return "Thredds";
    }

    /**
     * _more_
     *
     * @param request _more_
     * @param entry _more_
     * @param metadata _more_
     * @param doc _more_
     * @param datasetNode _more_
     *
     * @throws Exception _more_
     */
    public void addMetadataToCatalog(Request request, Entry entry,
                                     Metadata metadata, Document doc,
                                     Element datasetNode)
            throws Exception {
        Metadata.Type type = getType(metadata.getType());
        if (type.equals(TYPE_LINK)) {
            XmlUtil.create(doc, getTag(TYPE_DOCUMENTATION), datasetNode,
                           new String[] { "xlink:href",
                                          metadata.getAttr2(), "xlink:title",
                                          metadata.getAttr1() });
        } else if (type.equals(TYPE_DOCUMENTATION)) {
            //            System.err.println ("tag:" +  getTag(TYPE_DOCUMENTATION));
            XmlUtil.create(doc, getTag(TYPE_DOCUMENTATION), datasetNode,
                           metadata.getAttr2(), new String[] { ATTR_TYPE,
                                                               metadata.getAttr1() });
        } else if (type.equals(TYPE_PROPERTY)) {
            XmlUtil.create(doc, getTag(TYPE_PROPERTY), datasetNode,
                           new String[] { ATTR_NAME,
                                          metadata.getAttr1(), ATTR_VALUE,
                                          metadata.getAttr2() });
        } else if (type.equals(TYPE_KEYWORD)) {
            XmlUtil.create(doc, getTag(TYPE_KEYWORD), datasetNode,
                           metadata.getAttr1());
        } else if (type.equals(TYPE_CONTRIBUTOR)) {
            XmlUtil.create(doc, getTag(TYPE_DOCUMENTATION), datasetNode,
                           metadata.getAttr1(), new String[] { ATTR_ROLE,
                    metadata.getAttr2() });
        } else if (type.equals(TYPE_ICON)) {
            XmlUtil.create(doc, getTag(TYPE_DOCUMENTATION), datasetNode,
                           new String[] { "xlink:href",
                                          metadata.getAttr1(), "xlink:title",
                                          "icon" });
        } else if (type.equals(TYPE_PUBLISHER) || type.equals(TYPE_CREATOR)) {
            Element node = XmlUtil.create(doc, getTag(type), datasetNode);
            XmlUtil.create(doc, CatalogOutputHandler.TAG_NAME, node,
                           metadata.getAttr1(), new String[] { ATTR_ROLE,
                    metadata.getAttr2() });
            XmlUtil.create(doc, CatalogOutputHandler.TAG_CONTACT, node,
                           new String[] { ATTR_EMAIL,
                                          metadata.getAttr3(), ATTR_URL,
                                          metadata.getAttr4() });
        }
    }


    /**
     * _more_
     *
     * @param type _more_
     *
     * @return _more_
     */
    public boolean xxcanHandle(String type) {
        if (super.canHandle(type)) {
            return true;
        }
        //For now
        return super.canHandle("thredds." + type);
    }


    /**
     * _more_
     *
     * @param metadata _more_
     *
     * @return _more_
     */
    public String[] getHtml(Metadata metadata) {
        Metadata.Type type    = getType(metadata.getType());
        String        lbl     = msgLabel(type.getLabel());
        String        content = null;
        if (type.equals(TYPE_LINK)) {
            content = HtmlUtil.href(metadata.getAttr2(), metadata.getAttr1());
        } else if (type.equals(TYPE_DOCUMENTATION)) {
            if (metadata.getAttr1().length() > 0) {
                lbl = msgLabel(getLabel(metadata.getAttr1()));
            }
            content = metadata.getAttr2();
        } else if (type.equals(TYPE_PROPERTY)) {
            lbl     = msgLabel(getLabel(metadata.getAttr1()));
            content = metadata.getAttr2();
        } else if (type.equals(TYPE_ICON)) {
            lbl     = "";
            content = HtmlUtil.img(metadata.getAttr1());
        } else if (type.equals(TYPE_PUBLISHER) || type.equals(TYPE_CREATOR)) {
            content = metadata.getAttr1();
            if (metadata.getAttr3().length() > 0) {
                content += HtmlUtil.br() + msgLabel("Email")
                           + HtmlUtil.space(1) + metadata.getAttr3();
            }
            if (metadata.getAttr4().length() > 0) {
                content += HtmlUtil.br() + msgLabel("URL")
                           + HtmlUtil.space(1)
                           + HtmlUtil.href(metadata.getAttr4(),
                                           metadata.getAttr4());
            }
        } else {
            content = metadata.getAttr1();
        }
        if (content == null) {
            return null;
        }
        return new String[] { lbl, content };
    }





    /**
     * _more_
     *
     * @param request _more_
     * @param sb _more_
     * @param type _more_
     * @param doSelect _more_
     *
     * @throws Exception _more_
     */
    public void addToSearchForm(Request request, StringBuffer sb,
                                Metadata.Type type, boolean doSelect)
            throws Exception {
        sb.append(HtmlUtil.hidden(ARG_METADATA_TYPE + "." + type,
                                  type.toString()));
        String inheritedCbx = HtmlUtil.checkbox(ARG_METADATA_INHERITED + "."
                                  + type, "true", false) + HtmlUtil.space(1)
                                      + "inherited";
        inheritedCbx = "";

        if (doSelect) {
            String[] values = getMetadataManager().getDistinctValues(request,
                                  this, type);
            if ((values == null) || (values.length == 0)) {
                return;
            }
            List l = Misc.toList(values);
            l.add(0, new TwoFacedObject(msg("None"), ""));
            sb.append(HtmlUtil.formEntry(msgLabel(type.getLabel()),
                                         HtmlUtil.select(ARG_METADATA_ATTR1
                                             + "." + type, l, "",
                                                 100) + inheritedCbx));
        } else {
            sb.append(
                HtmlUtil.formEntry(
                    msgLabel(type.getLabel()),
                    HtmlUtil.input(ARG_METADATA_ATTR1 + "." + type, "")
                    + inheritedCbx));
        }

    }


    /**
     * _more_
     *
     * @param request _more_
     * @param sb _more_
     *
     * @throws Exception _more_
     */
    public void addToSearchForm(Request request, StringBuffer sb)
            throws Exception {
        addToSearchForm(request, sb, TYPE_DOCUMENTATION, false);
        addToSearchForm(request, sb, TYPE_KEYWORD, true);
        addToSearchForm(request, sb, TYPE_PROJECT, true);
        addToSearchForm(request, sb, TYPE_CREATOR, true);
        addToSearchForm(request, sb, TYPE_CONTRIBUTOR, true);
        addToSearchForm(request, sb, TYPE_PUBLISHER, true);
    }


    /**
     * _more_
     *
     * @param cols _more_
     *
     * @return _more_
     */
    private String formEntry(String[] cols) {
        if (cols.length == 2) {
            //            return HtmlUtil.rowTop(HtmlUtil.cols(cols[0])+"<td colspan=2>" + cols[1] +"</td>");
            //            return HtmlUtil.rowTop(HtmlUtil.cols(cols[0])
            //                                   + "<td xxcolspan=2>" + cols[1] + "</td>");
        }
        StringBuffer sb = new StringBuffer();

        sb.append(HtmlUtil.rowTop("<td colspan=2>" + cols[0] + "</td>"));
        for (int i = 1; i < cols.length; i += 2) {
            if (false && (i == 1)) {
                sb.append(
                    HtmlUtil.rowTop(
                        HtmlUtil.cols(cols[0])
                        + "<td class=\"formlabel\" align=right>" + cols[i]
                        + "</td>" + "<td>" + cols[i + 1]));
            } else {
                //                sb.append(HtmlUtil.rowTop("<td></td><td class=\"formlabel\" align=right>" + cols[i] +"</td>" +
                //                                          "<td>" + cols[i+1]));
                sb.append(
                    HtmlUtil.rowTop(
                        "<td class=\"formlabel\" align=right>" + cols[i]
                        + "</td>" + "<td>" + cols[i + 1]));
            }
        }
        return sb.toString();
    }


    /**
     * _more_
     *
     *
     * @param request _more_
     * @param metadata _more_
     * @param forEdit _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public String[] getForm(Request request, Metadata metadata,
                            boolean forEdit)
            throws Exception {
        Metadata.Type type    = getType(metadata.getType());
        String        lbl     = msgLabel(type.getLabel());
        String        content = null;
        String        id      = metadata.getId();
        String        suffix  = "";
        if (id.length() > 0) {
            suffix = "." + id;
        }


        String submit = HtmlUtil.submit(msg("Add") + HtmlUtil.space(1)
                                        + type.getLabel());
        String cancel = HtmlUtil.submit(msg("Cancel"), ARG_CANCEL);
        if (forEdit) {
            submit = "";
            cancel = "";
        }
        String arg1 = ARG_ATTR1 + suffix;
        String arg2 = ARG_ATTR2 + suffix;
        String arg3 = ARG_ATTR3 + suffix;
        String arg4 = ARG_ATTR4 + suffix;
        String size = HtmlUtil.SIZE_70;

        if (type.equals(TYPE_LINK)) {
            content = formEntry(new String[] { submit, msgLabel("Label"),
                    HtmlUtil.input(arg1, metadata.getAttr1(), size),
                    msgLabel("Url"),
                    HtmlUtil.input(arg2, metadata.getAttr2(), size) });
        } else if (type.equals(TYPE_ICON)) {
            content = formEntry(new String[] { submit, msgLabel("URL"),
                    HtmlUtil.input(arg1, metadata.getAttr1(), size) });
        } else if (type.equals(TYPE_DOCUMENTATION)) {
            List types = Misc.newList(
                             new TwoFacedObject(msg("Summary"), "summary"),
                             new TwoFacedObject(msg("Funding"), "funding"),
                             new TwoFacedObject(msg("History"), "history"),
                             new TwoFacedObject(
                                 msg("Processing Level"),
                                 "processing_level"), new TwoFacedObject(
                                     msg("Rights"), "rights"));

            content = formEntry(new String[] { submit, msgLabel("Type"),
                    HtmlUtil.select(arg1, types, metadata.getAttr1()),
                    msgLabel("Value"),
                    HtmlUtil.textArea(arg2, metadata.getAttr2(), 5, 50) });
        } else if (type.equals(TYPE_CONTRIBUTOR)) {
            content = formEntry(new String[] { submit, msgLabel("Name"),
                    HtmlUtil.input(arg1, metadata.getAttr1(), size),
                    msgLabel("Role"),
                    HtmlUtil.input(arg2, metadata.getAttr2(), size) });
        } else if (type.equals(TYPE_PROPERTY)) {
            content = formEntry(new String[] { submit, msgLabel("Name"),
                    HtmlUtil.input(arg1, metadata.getAttr1(), size),
                    msgLabel("Value"),
                    HtmlUtil.input(arg2, metadata.getAttr2(), size) });

        } else if (type.equals(TYPE_KEYWORD)) {
            content = formEntry(new String[] { submit, msgLabel("Value"),
                    HtmlUtil.input(arg1,
                                   metadata.getAttr1().replace("\n", ""),
                                   size),
                    msgLabel("Vocabulary"),
                    HtmlUtil.input(arg2, metadata.getAttr2(), size) });

        } else if (type.equals(TYPE_PUBLISHER) || type.equals(TYPE_CREATOR)) {
            content = formEntry(new String[] {
                submit, msgLabel("Organization"),
                HtmlUtil.input(arg1, metadata.getAttr1(), size),
                msgLabel("Email"),
                HtmlUtil.input(arg3, metadata.getAttr3(), size),
                msgLabel("URL"),
                HtmlUtil.input(arg4, metadata.getAttr4(), size)
            });
        } else {
            content = formEntry(new String[] { submit, msgLabel("Value"),
                    HtmlUtil.input(arg1, metadata.getAttr1(), size) });
        }
        if (content == null) {
            return null;
        }
        String argtype = ARG_TYPE + suffix;
        String argid   = ARG_METADATAID + suffix;
        content = content + HtmlUtil.hidden(argtype, type.getType())
                  + HtmlUtil.hidden(argid, metadata.getId());
        if (cancel.length() > 0) {
            content = content + HtmlUtil.row(HtmlUtil.colspan(cancel, 2));
        }
        return new String[] { lbl, content };
    }


    /**
     * _more_
     *
     * @param tag _more_
     * @param type _more_
     *
     * @return _more_
     */
    public boolean isTag(String tag, Metadata.Type type) {
        return ("thredds." + tag).toLowerCase().equals(type.getType());
    }


    /**
     * _more_
     *
     * @param child _more_
     *
     * @return _more_
     */
    public Metadata makeMetadataFromCatalogNode(Element child) {
        String tag = child.getTagName();
        if (isTag(tag, TYPE_DOCUMENTATION)) {
            if (XmlUtil.hasAttribute(child, "xlink:href")) {
                String url = XmlUtil.getAttribute(child, "xlink:href");
                return new Metadata(getRepository().getGUID(), "", TYPE_LINK,
                                    XmlUtil.getAttribute(child,
                                        "xlink:title", url), url, "", "");
            } else {
                String type = XmlUtil.getAttribute(child, "type","summary");
                String text = XmlUtil.getChildText(child).trim();
                return new Metadata(getRepository().getGUID(), "",
                                    TYPE_DOCUMENTATION, type, text, "", "");
            }
        } else if (isTag(tag, TYPE_PROJECT)) {
            String text = XmlUtil.getChildText(child).trim();
            return new Metadata(getRepository().getGUID(), "", TYPE_PROJECT,
                                text,
                                XmlUtil.getAttribute(child, ATTR_VOCABULARY,
                                    ""), "", "");
        } else if (isTag(tag, TYPE_CONTRIBUTOR)) {
            String text = XmlUtil.getChildText(child).trim();
            return new Metadata(getRepository().getGUID(), "",
                                TYPE_CONTRIBUTOR, text,
                                XmlUtil.getAttribute(child, ATTR_ROLE, ""),
                                "", "");
        } else if (isTag(tag, TYPE_PUBLISHER) || isTag(tag, TYPE_CREATOR)) {
            Element nameNode = XmlUtil.findChild(child,
                                   CatalogOutputHandler.TAG_NAME);
            String name = XmlUtil.getChildText(nameNode).trim();
            String vocabulary = XmlUtil.getAttribute(nameNode,
                                    ATTR_VOCABULARY, "");
            String email = "";
            String url   = "";
            Element contactNode = XmlUtil.findChild(child,
                                      CatalogOutputHandler.TAG_CONTACT);
            if (contactNode != null) {
                email = XmlUtil.getAttribute(contactNode, ATTR_EMAIL, "");
                url   = XmlUtil.getAttribute(contactNode, ATTR_URL, "");
            }
            return new Metadata(getRepository().getGUID(), "", getType("thredds."+tag),
                                name, vocabulary, email, url);
        } else if (isTag(tag, TYPE_KEYWORD)) {
            String text = XmlUtil.getChildText(child).trim();
            //Some of the catalogs have new lines in the keyword
            text = text.replace("\r\n", " ");
            text = text.replace("\n", " ");
            return new Metadata(getRepository().getGUID(), "", TYPE_KEYWORD,
                                text,
                                XmlUtil.getAttribute(child, ATTR_VOCABULARY,
                                    ""), "", "");

        } else if (isTag(tag, TYPE_AUTHORITY) || isTag(tag, TYPE_DATATYPE)
                   || isTag(tag, TYPE_DATAFORMAT)) {
            String text = XmlUtil.getChildText(child).trim();
            text = text.replace("\n", "");
            return new Metadata(getRepository().getGUID(), "", getType("thredds."+tag),
                                text, "", "", "");
        } else if (isTag(tag, TYPE_PROPERTY)) {
            return new Metadata(getRepository().getGUID(), "", getType("thredds."+tag),
                                XmlUtil.getAttribute(child, ATTR_NAME),
                                XmlUtil.getAttribute(child, ATTR_VALUE), "",
                                "");
        }
        return null;
    }




}

