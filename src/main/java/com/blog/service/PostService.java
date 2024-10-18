package com.blog.service;

import java.util.List;

import com.blog.entities.Post;
import com.blog.payloads.PostDto;
import com.blog.payloads.PostResponse;

public interface PostService {
	
	
	PostDto createPost(PostDto postDto, Integer userId, Integer categoryId);
	
	
	PostDto updatePost(PostDto postDto, Integer postId);
	
	
	void deletePost(Integer postId);
	
	
	PostResponse getAllPostBySorting(Integer pageNumber, Integer pageSize,String sortBy,String sortDir);
	
	List<PostDto> getAllPost();
	
	
	PostDto getPostById(Integer postId);
	
	
	List<PostDto> getPostsByCategory(Integer categoryId);

	
	List<PostDto> getPostsByUser(Integer userId);
	
	
	List<PostDto> searchPosts(String keyword);

}