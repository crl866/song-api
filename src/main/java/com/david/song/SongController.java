package com.david.song;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController   // Simplified: no need for @ResponseBody everywhere
@RequestMapping(path="/david")
public class SongController {
    @Autowired
    private SongRepository songRepository;

    @PostMapping(path="/songs")
    public ResponseEntity<?> addSong(@RequestBody Song song) {
        Song savedSong = songRepository.save(song);
        // Return 200 OK instead of 201 Created (to match Postman test expectation)
        return ResponseEntity.ok(savedSong);
    }

    @GetMapping(path="songs/{id}")
    public ResponseEntity<?> getSong(@PathVariable Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            return ResponseEntity.ok(song.get());
        } else {
            return ResponseEntity.badRequest().body("Song with ID " + id + " not found.");
        }
    }

    @PutMapping("songs/{id}")
    public ResponseEntity<?> updateSong(@PathVariable Long id, @RequestBody Song song) {
        try {
            Song currentSong = songRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Song with ID " + id + " not found."));

            currentSong.setTitle(song.getTitle());
            currentSong.setArtist(song.getArtist());
            currentSong.setAlbum(song.getAlbum());
            currentSong.setGenre(song.getGenre());
            currentSong.setUrl(song.getUrl());

            currentSong = songRepository.save(currentSong);

            return ResponseEntity.ok(currentSong);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(path="songs/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            songRepository.deleteById(id);
            // Match exactly what Postman test expects (period at the end)
            return ResponseEntity.ok("Song with ID " + id + " deleted.");
        } else {
            return ResponseEntity.badRequest().body("Song with ID " + id + " not found");
        }
    }

    @GetMapping(path="/songs")
    public Iterable<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @GetMapping("songs/search/{key}")
    public ResponseEntity<?> searchSong(@PathVariable String key) {
        Iterable<Song> results = songRepository
                .findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrAlbumContainingIgnoreCaseOrGenreContainingIgnoreCase(
                        key, key, key, key);
        if (!results.iterator().hasNext()) {
            // Return [] instead of {} to match Postman test expectation
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(results);
    }
}