package com.blog.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.blog.payloads.ApiResponse;
import com.blog.payloads.PostDto;
import com.blog.payloads.PostResponse;
import com.blog.service.FileService;
import com.blog.service.PostService;
import com.blog.utils.AppConstants;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class PostController {

	@Autowired
	private PostService postService;
	
	@Autowired
	private FileService fileService;

	@Value("${project.image}") // from '.properties'
	private String path;

	//Ctr+A for assign variable
	@PostMapping("/user/{userId}/category/{categoryId}/posts") // kon user, kis category me post kiya h
	public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @PathVariable Integer userId,
			@PathVariable Integer categoryId) {
		PostDto createdPost = this.postService.createPost(postDto, userId, categoryId);
		return new ResponseEntity<PostDto>(createdPost, HttpStatus.CREATED);
	}

	// get all post by a user
	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Integer userId) {
		List<PostDto> posts = this.postService.getPostsByUser(userId);
		return new ResponseEntity<List<PostDto>>(posts, HttpStatus.OK);
	}

	// get by category
	@GetMapping("/category/{categoryId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Integer categoryId) {
		List<PostDto> posts = this.postService.getPostsByCategory(categoryId);
		return new ResponseEntity<List<PostDto>>(posts, HttpStatus.OK);
	}

	// get by category
	@GetMapping("/post/{postId}")
	public ResponseEntity<PostDto> getPostsById(@PathVariable Integer postId) {
		PostDto posts = this.postService.getPostById(postId);
		return new ResponseEntity<PostDto>(posts, HttpStatus.OK);
	}

	// get by category
	@GetMapping("/posts")
	public ResponseEntity<List<PostDto>> getAllPosts() {
		List<PostDto> posts = this.postService.getAllPost();
		return new ResponseEntity<List<PostDto>>(posts, HttpStatus.OK);
	}
	
	@PutMapping("/post/update/{postId}")
	public ResponseEntity<PostDto> updatePostByID(@RequestBody PostDto postDto ,@PathVariable Integer postId) {
		PostDto posts = this.postService.updatePost(postDto,postId);
		return new ResponseEntity<PostDto>(posts, HttpStatus.OK);
	}
	@DeleteMapping("/post/delete/{postId}")
	public ApiResponse deletePost(@PathVariable Integer postId)
	{
		this.postService.deletePost(postId);
		return new ApiResponse("Post deleted successfully !!", true);
	}

	// get all posts
	@GetMapping("/pagination/posts")
	public ResponseEntity<PostResponse> getAllPostByPagination(
			@RequestParam(value = "PageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {
		PostResponse postResponse = this.postService.getAllPostByPagination(pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<PostResponse>(postResponse, HttpStatus.OK);
	}

	// search based on keyword in title
	@GetMapping("/posts/search/{keywords}")
	public ResponseEntity<List<PostDto>> searchPostByTitle(@PathVariable String keywords) {
		List<PostDto> result = this.postService.searchPosts(keywords);
		return new ResponseEntity<List<PostDto>>(result, HttpStatus.OK);
	}
	
	// post image upload
	// 'postid' lena hoga taki chale pta ye image kis post ka h.(isko url me pass
	// karenge)
	// image ko parameter me pass karenge.

	@PostMapping("/post/image/upload/{postId}")
	public ResponseEntity<PostDto> uploadPostImage(@RequestParam("image") MultipartFile image,
			@PathVariable Integer postId) throws IOException {
		// get the post first. Agar nhi mila to yhi pe exception throw kar dega.
		PostDto postDto = this.postService.getPostById(postId);

		// get the fileName by which we have to save in database
		String fileName = fileService.uploadImage(path, image);

		// ab isko database me save karna h.

		postDto.setImageName(fileName);
		// ab save kar do update call karke
		PostDto updatedPost = this.postService.updatePost(postDto, postId);
		return new ResponseEntity<PostDto>(updatedPost, HttpStatus.OK);

	}

	// method to serve files
	// must run in browser like : http://localhost:9090/file/profiles/image_name.jpg
	// image_name you can find from folder directory or from user details.

	@GetMapping(value = "/post/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable("imageName") String imageName, HttpServletResponse response)
			throws IOException {

		InputStream resource = this.fileService.getResource(path, imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream()); // kisko , kahan bhejna h.
	}

}