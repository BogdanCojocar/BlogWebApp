package org.home.data;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class BlogPostDAO {

	private final MongoOperations operations;

	private final static Logger LOG = Logger.getLogger(BlogPostDAO.class
			.getName());

	@Autowired
	public BlogPostDAO(MongoOperations operations) {
		Assert.notNull(operations);
		this.operations = operations;
		LOG.info("BlogPostDAO created");
	}

	public String addPost(String title, String body, List<String> tags, String author) {
		String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
		permalink = permalink.replaceAll("\\W", ""); // get rid of non
														// alphanumeric
		permalink = permalink.toLowerCase();

		BlogPostDTO post = new BlogPostDTO();
		post.setTitle(title);
		post.setAuthor(author);
		post.setBody(body);
		post.setPermalink(permalink);
		post.setTags(tags);
		post.setComments(new ArrayList<CommentDTO>());
		post.setDate(new Date());

		try {
			operations.save(post);
			LOG.info("New post added: " + post);
			return permalink;
		} catch (DuplicateKeyException e) {
			LOG.info("exception: " + e);
			LOG.info("Post already in db " + post);
			return "";
		}
	}

	public BlogPostDTO findPostByPermalink(String permalink) {
		Query query = query(where("permalink").is(permalink));
		return operations.findOne(query, BlogPostDTO.class);
	}
	
	public void removePostByPermalink(String permalink) {
		Query query = query(where("permalink").is(permalink));
		operations.remove(query, BlogPostDTO.class);
	}

	public List<BlogPostDTO> findPostByDateDescending(int limit) {
		Query query = new Query();
		query.limit(limit);
		query.with(new Sort(Sort.Direction.DESC, "date"));
		return operations.find(query, BlogPostDTO.class);
	}

	public List<BlogPostDTO> findPostByTagWithDateDescending(String tag,
			int limit) {
		Query query = new Query();
		query.limit(limit).addCriteria(where("tags").is(tag));
		query.with(new Sort(Sort.Direction.DESC, "date"));
		return operations.find(query, BlogPostDTO.class);
	}

	public void addPostComment(String author, String body, String email,
			String permalink) {
		CommentDTO comment = new CommentDTO(author, body, email);
		operations.updateFirst(query(where("permalink").is(permalink)),
				new Update().push("comments", comment), BlogPostDTO.class);
	}
	
	public void clearCollection() {
		operations.remove(new Query(), "blogpost");
	}
}
