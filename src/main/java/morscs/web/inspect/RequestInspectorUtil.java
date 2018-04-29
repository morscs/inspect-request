/*
 *  RequestInspectorUtil.java
 *
 *    Copyright 2018 Moriarty Software & Consulting Services
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package morscs.web.inspect;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * Useful for dumping ServletRequest parameters for debugging.
 */
public class RequestInspectorUtil {
  private final String eol; // end of line
  private final String indent; // line indent
  private final String par; // paragraph
  private final String nvp; // name = value for example
  private final String vsep; // value separator

  /**
   * @param eol end of line characters
   * @param indent indent characters
   * @param par paragraph separator
   * @param nvp separator between name and value in a pair
   * @param vsep vertical separator between logical elements on the same line (ex. comma char)
   */
  public RequestInspectorUtil(String eol, String indent, String par, String nvp, String vsep) {
    this.eol = eol;
    this.indent = indent;
    this.par = par;
    this.nvp = nvp;
    this.vsep = vsep;
  }

  /**
   * Formats name value pair.
   *
   * @param name name
   * @param value value
   * @return formatted pair
   */
  public String nvpOut(String name, String value) {
    return indent + name + nvp + value + eol;
  }

  /**
   * Produce string representation of http request contents.
   *
   * @param request servlet request
   * @return formatted contents.
   */
  public String dumpRequestContent(HttpServletRequest request) {
    StringBuilder buf = new StringBuilder();

    // ----------
    buf.append(par).append("GENERAL").append(eol);

    buf.append(nvpOut("ServerName", request.getServerName()));
    buf.append(nvpOut("ServerPort", String.valueOf(request.getServerPort())));
    buf.append(nvpOut("Scheme", request.getScheme()));
    buf.append(nvpOut("Protocol", request.getProtocol()));
    buf.append(nvpOut("PathInfo", request.getPathInfo()));
    buf.append(nvpOut("PathTranslated", request.getPathTranslated()));
    buf.append(nvpOut("ServletPath", request.getServletPath()));
    buf.append(nvpOut("ContextPath", request.getContextPath()));
    buf.append(nvpOut("RequestURI", request.getRequestURI()));
    buf.append(nvpOut("AuthType", request.getAuthType()));
    buf.append(nvpOut("ContentType", request.getContentType()));
    buf.append(nvpOut("ContentLength", String.valueOf(request.getContentLength())));
    buf.append(nvpOut("QueryString", request.getQueryString()));
    buf.append(nvpOut("RemoteUser", request.getRemoteUser()));
    buf.append(nvpOut("RequestedSessionId", request.getRequestedSessionId()));
    buf.append(nvpOut("Method", request.getMethod()));

    // --------
    buf.append(par).append("LOCAL").append(eol);
    buf.append(nvpOut("LocalAddr", request.getLocalAddr()));
    buf.append(nvpOut("LocalName", request.getLocalName()));
    buf.append(nvpOut("LocalPort", String.valueOf(request.getLocalPort())));

    // ----------
    buf.append(par).append("REMOTE").append(eol);
    buf.append(nvpOut("RemoteUser", request.getRemoteUser()));
    buf.append(nvpOut("RemoteAddr", request.getRemoteAddr()));
    buf.append(nvpOut("RemoteHost", request.getRemoteHost()));
    buf.append(nvpOut("RemotePort", String.valueOf(request.getRemotePort())));

    //---------
    buf.append(par).append("ATTRIBUTES").append(eol);
    Enumeration<String> atts = request.getAttributeNames();
    while (atts.hasMoreElements()) {
      String name = atts.nextElement();
      buf.append(indent).append(name).append(nvp).append(request.getAttribute(name)).append(eol);
      if ("javax.servlet.request.X509Certificate".equals(name)) {
        X509Certificate[] certObj = (X509Certificate[]) request.getAttribute(name);
        //                certObj.getNotAfter(); // Date
        //                certObj.getNotBefore(); //Date
        //                certObj.getSerialNumber(); // BigInteger
        //                certObj.getSigAlgName(); // String
        //                certObj.getSigAlgOID(); //String
        //                certObj.getSubjectDN(); //Principal
        //                certObj.getVersion(); //int
        //                certObj.getPublicKey(); //PublicKey
        for (int i = 0; i < certObj.length; ++i) {
          buf.append(indent).append(indent).append("CERT-").append(i).append(eol);
          Principal subject = certObj[i].getSubjectDN();
          buf.append(indent)
              .append(nvpOut("SubjectDN", subject == null ? null : subject.getName()));
          Principal issuer = certObj[i].getIssuerDN();
          buf.append(indent).append(nvpOut("IssuerDN", issuer == null ? "null" : issuer.getName()));
        }
      }
    }

    //--------
    buf.append(par).append("HEADERS").append(eol);
    Enumeration<String> headers = request.getHeaderNames();
    while (headers.hasMoreElements()) {
      String header = headers.nextElement();
      buf.append(indent).append(header).append(nvp);
      Enumeration<String> hvals = request.getHeaders(header);
      while (hvals.hasMoreElements()) {
        String hval = hvals.nextElement();
        buf.append(hval).append(vsep);
      }
      buf.append(eol);
    }

    //--------
    buf.append(par).append("PARAMETERS").append(eol);
    Enumeration<String> pnames = request.getParameterNames();
    Map<String, String[]> pmap = request.getParameterMap();
    while (pnames.hasMoreElements()) {
      String pname = pnames.nextElement();
      buf.append(indent).append(pname).append(nvp);
      String[] pvals = pmap.get(pname);
      for (String pval : pvals) {
        buf.append(pval).append(vsep);
      }
      buf.append(eol);
    }

    request.getCookies(); //Cookie[]
    //request.getParts(); //Collection<Part)

    request.getSession();//HttpSession
    Principal pal = request.getUserPrincipal(); //Principal
    buf.append(par).append("PRINCIPAL").append(eol);
    buf.append(indent).append(nvpOut("Principal", pal == null ? "null" : pal.getName()));
    buf.append(indent)
        .append(nvpOut("Principal Class", pal == null ? "null" : pal.getClass().getName()));

    return buf.toString();
  }

}
