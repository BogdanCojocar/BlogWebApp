package org.home.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMongoConfig.class)
public class BlogPostDAOTest {
	
	private static final String TAG = "tag1";
	private static final String TITLE = "title";
	private static final String AUTHOR = "author";
	private static final String BODY = "body";
	private static final List<String> TAGS = Arrays.asList(TAG, "tag2");
	private static final String PERMALINK = "title";
	private static final String EMAIL = "email";
	
	@Autowired
	private BlogPostDAO blogPostDAO;
	
	@Before
	public void init() {
		blogPostDAO.clearCollection();
	}
	
	private void addFivePosts() {
		blogPostDAO.clearCollection();
		for (int i = 0; i < 5; i++) {
			blogPostDAO.addPost(TITLE+i, BODY+i, TAGS, AUTHOR+i);
		}
	}
	
	@Test
	public void testBlogPostDAOInsertion() {
		blogPostDAO.addPost(TITLE, BODY, TAGS, AUTHOR);
		BlogPostDTO blogPost = blogPostDAO.findPostByPermalink(PERMALINK);
		
		assertThat(blogPost.getAuthor(), is(equalTo(AUTHOR)));
		blogPostDAO.removePostByPermalink(PERMALINK);
	}
	
	@Test
	public void testBlogPostDAODuplicateKeyException() {
		blogPostDAO.removePostByPermalink(PERMALINK);
		blogPostDAO.addPost(TITLE, BODY, TAGS, AUTHOR);
		assertThat(blogPostDAO.addPost(TITLE, BODY, TAGS, AUTHOR), is(equalTo("")));
	}
	
	@Test 
	public void testDateDecendingQuery() {
		addFivePosts();
		List<BlogPostDTO> postList = blogPostDAO.findPostByDateDescending(3);
		
		assertThat(postList.size(), is(3));
		assertThat(postList.get(0).getAuthor(), is(equalTo(AUTHOR+4)));
		assertThat(postList.get(1).getAuthor(), is(equalTo(AUTHOR+3)));
		assertThat(postList.get(2).getAuthor(), is(equalTo(AUTHOR+2)));
	}
	
	@Test 
	public void testDateDecendingWithTagQuery() {
		addFivePosts();
		List<BlogPostDTO> postList = blogPostDAO.findPostByTagWithDateDescending(TAG, 2);
		
		assertThat(postList.size(), is(2));
		assertThat(postList.get(0).getTags().get(0), is(equalTo(TAG)));
	}
	
	@Test
	public void testCommentInsertion() {
		CommentDTO expectedComment = new CommentDTO(AUTHOR, BODY, EMAIL);
		blogPostDAO.removePostByPermalink(PERMALINK);
		blogPostDAO.addPost(TITLE, BODY, TAGS, AUTHOR);
		blogPostDAO.addPostComment(AUTHOR, BODY, EMAIL, PERMALINK);
		BlogPostDTO post = blogPostDAO.findPostByPermalink(PERMALINK);
		
		assertThat(post.getComments().size(), is(1));
		assertThat(post.getComments().get(0), is(equalTo(expectedComment)));
		
	}
}
