package herokuSamples.web.mail;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import herokuSamples.util.TemplateEngine;
import herokuSamples.util.FileUtils;
import herokuSamples.util.Cache;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

@WebServlet(name="InboundMailServlet", urlPatterns={"/inboundMail"})
public class InboundMailServlet extends HttpServlet {

	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "インバウンドメール");
		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if ("true".equals(req.getParameter("delete"))) {
			Cache.remove("latestMail");
			res.sendRedirect("/inboundMail");
			return;
		}
		Map<String, Object> params = initParams();
		String mail = (String)Cache.get("latestMail");
		if (mail == null) {
			mail = "No mail";
		}
		params.put("mail", mail);
		TemplateEngine.merge(res, "mail/inboundMail.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		try {
			ReceivedMail mail = new ReceivedMail(upload.parseRequest(req));
			int attachmentCount = mail.getAttachmentCount();

			StringBuilder buf = new StringBuilder();
			buf.append("From: ").append(mail.getFrom()).append("\n")
				.append("To: ").append(mail.getTo()).append("\n")
				.append("Subject: ").append(mail.getSubject()).append("\n")
				.append("AttachmentCount: " + attachmentCount).append("\n");
			for (int i=0; i<attachmentCount; i++) {
				buf.append("Attachment").append(i+1).append(": ")
					.append(mail.getAttachmentName(i)).append("\n");
			}
			String body = mail.getTextBody();
			buf.append("\n").append(body);
			Cache.set("latestMail", buf.toString(), 7200);
			System.out.println(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class ReceivedMail {
		private List<FileItem> items;
		private Map<String, String> charsets = null;
		private Map<String, AttachmentInfo> attachmentInfo = null;

		public ReceivedMail(List<FileItem> items) {
			this.items = items;
		}

		public String getCharset(String name) {
			if (this.charsets == null) {
				FileItem item = getFileItem("charsets");
				if (item == null) {
					this.charsets = new HashMap<String, String>();
				} else {
					Type type = new TypeToken<Map<String, String>>() {}.getType();
					try {
						this.charsets = new Gson().fromJson(item.getString("utf-8"), type);
					} catch (UnsupportedEncodingException e) {
						throw new IllegalStateException(e);
					}
				}
			}
			return this.charsets.get(name);
		}

		public AttachmentInfo getAttachmentInfo(int n) {
			if (this.attachmentInfo == null) {
				String json = getValue("attachment-info");
				if (json == null) {
					this.attachmentInfo = new HashMap<String, AttachmentInfo>();
				} else {
					Type type = new TypeToken<Map<String, AttachmentInfo>>() {}.getType();
					this.attachmentInfo = new Gson().fromJson(json, type);
				}
			}
			return this.attachmentInfo.get("attachment" + (n + 1));
		}

		public String getValue(String name) {
			for (FileItem item : items) {
				if (item.getFieldName().equals(name)) {
					try {
						String charset = getCharset(name);
						return item.getString(charset == null ? "utf-8" : charset);
					} catch (UnsupportedEncodingException e) {
						throw new IllegalStateException(e);
					}
				}
			}
			return null;
		}

		public FileItem getFileItem(String name) {
			for (FileItem item : items) {
				if (item.getFieldName().equals(name)) {
					return item;
				}
			}
			return null;
		}

		public List<String> keys() {
			List<String> list = new ArrayList<String>();
			for (FileItem item : items) {
				list.add(item.getFieldName());
			}
			return list;
		}

		public String getHeaders() { return getValue("headers");}
		public String getTextBody() { return getValue("text");}
		public String getHtmlBody() { return getValue("html");}
		public String getFrom() { return getValue("from");}
		public String getTo() { return getValue("to");}
		public String getCc() { return getValue("cc");}
		public String getSubject() { return getValue("subject");}

		public int getAttachmentCount() {
			String n = getValue("attachments");
			return n == null ? 0 : Integer.parseInt(n);
		}

		public InputStream getAttachmentStream(int n) throws IOException {
			FileItem item = getFileItem("attachment" + (n + 1));
			return item != null && !item.isFormField() ? item.getInputStream() : null;
		}

		public String getAttachmentName(int n) {
			AttachmentInfo info = getAttachmentInfo(n);
			return info != null ? info.getName() : null;
		}

	}

	public static class AttachmentInfo {
		private String filename;
		private String name;
		private String type;

		public String getFilename() { return this.filename;}
		public String getName() { return this.name;}
		public String getType() { return this.type;}
	}
}
