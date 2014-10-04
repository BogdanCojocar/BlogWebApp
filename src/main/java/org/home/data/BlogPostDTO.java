package org.home.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blogpost")
public class BlogPostDTO {

	private String title;
	private String author;
	private String body;
	@Indexed(unique = true)
	private String permalink;
	private List<String> tags;
	private List<CommentDTO> comments;
	private Date date;

	public BlogPostDTO() {
		comments = new ArrayList<CommentDTO>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<CommentDTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentDTO> comments) {
		this.comments = comments;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "BlogPostDTO [title=" + title + ", author=" + author + ", body="
				+ body + ", permalink=" + permalink + ", tags=" + tags
				+ ", comments=" + comments + ", date=" + date + "]";
	}
}
