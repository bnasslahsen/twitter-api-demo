/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springdoc.demo.app3.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.demo.app3.dto.TweetDTO;
import org.springdoc.demo.app3.model.Tweet;
import org.springdoc.demo.app3.repository.TweetRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by bnasslahsen
 */
@RestController
public class TweetController {

	private final TweetRepository tweetRepository;

	private final TweetMapper tweetMapper;

	public TweetController(TweetRepository tweetRepository, TweetMapper tweetMapper) {
		this.tweetRepository = tweetRepository;
		this.tweetMapper = tweetMapper;
	}

	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "get All Tweets") })
	@GetMapping("/tweets")
	public List<TweetDTO> getAllTweets() {
		List<Tweet> tweet = tweetRepository.findAll();
		return tweetMapper.toDTOList(tweet);
	}

	@PostMapping("/tweets")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "create Tweets") })
	public TweetDTO createTweets(@Valid @RequestBody TweetDTO tweetDTO) {
		Tweet tweet = tweetRepository.save(tweetMapper.toEntity(tweetDTO));
		return tweetMapper.toDTO(tweet);
	}

	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "get Tweet By Id"),
			@ApiResponse(responseCode = "404", description = "tweet not found") })
	@GetMapping("/tweets/{id}")
	public ResponseEntity<TweetDTO> getTweetById(@PathVariable(value = "id") String tweetId) {
		return tweetRepository.findById(tweetId).map(savedTweet -> ResponseEntity.ok(tweetMapper.toDTO(savedTweet)))
				.orElse(ResponseEntity.notFound().build());
	}

	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "update Tweet"),
			@ApiResponse(responseCode = "404", description = "tweet not found") })
	@PutMapping("/tweets/{id}")
	public ResponseEntity<TweetDTO> updateTweet(@PathVariable(value = "id") String tweetId,
			@Valid @RequestBody TweetDTO tweetDTO) {
		Tweet tweetToUpdate = tweetRepository.findById(tweetId).orElse(null);
		if (tweetToUpdate != null) {
			tweetToUpdate.setText(tweetDTO.getText());
			tweetToUpdate = tweetRepository.save(tweetToUpdate);
			return new ResponseEntity<>(tweetMapper.toDTO(tweetToUpdate), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "delete Tweet"),
			@ApiResponse(responseCode = "404", description = "tweet not found") })
	@DeleteMapping("/tweets/{id}")
	public ResponseEntity deleteTweet(@PathVariable(value = "id") String tweetId) {
		Tweet tweetToDelete = tweetRepository.findById(tweetId).orElse(null);
		if (tweetToDelete != null) {
			tweetRepository.delete(tweetToDelete);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Operation(description = "Tweets are Sent to the client as Server Sent Events", responses = {
			@ApiResponse(responseCode = "200", description = "stream All Tweets") })
	@GetMapping(value = "/stream/tweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public List<TweetDTO> streamAllTweets() {
		return tweetMapper.toDTOList(tweetRepository.findAll());
	}

}
