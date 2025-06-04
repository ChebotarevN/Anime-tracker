package app.service;

import app.dao.AnimeDAO;
import app.model.Anime;
import app.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimeServiceTest {

    @Mock
    private AnimeDAO animeDao;

    @InjectMocks
    private AnimeService animeService;

    private Anime anime1;
    private Anime anime2;
    private Anime anime3;

    @BeforeEach
    void setUp() throws Exception {
        anime1 = new Anime(1, "http://img1.jpg", "Attack on Titan",
                Status.WATCHING, 25, 10);
        anime2 = new Anime(2, "http://img2.jpg", "Death Note",
                Status.COMPLETED, 37, 37);
        anime3 = new Anime(3, "http://img3.jpg", "One Piece",
                Status.PLAN_TO_WATCH, 1000, 0);
    }

    @Test
    void testGetAllAnimes() {
        // Подготовка
        when(animeDao.getAllAnimes()).thenReturn(Arrays.asList(anime1, anime2, anime3));

        // Выполнение
        List<Anime> result = animeService.getAllAnimes();

        // Проверка
        assertEquals(3, result.size());
        verify(animeDao, times(1)).getAllAnimes();
    }

    @Test
    void testGetAnimeById() {
        when(animeDao.getAnimeById(1)).thenReturn(anime1);

        Anime result = animeService.getAnimeById(1);

        assertNotNull(result);
        assertEquals("Attack on Titan", result.getTitle());
        verify(animeDao, times(1)).getAnimeById(1);
    }

    @Test
    void testAddAnime() throws Exception {
        Anime newAnime = new Anime(4, "http://img4.jpg", "Naruto",
                Status.WATCHING, 220, 50);

        animeService.addAnime(newAnime);

        verify(animeDao, times(1)).addAnime(newAnime);
    }

    @Test
    void testUpdateAnime() throws Exception {
        when(animeDao.getAnimeById(1)).thenReturn(anime1);
        Anime updatedAnime = new Anime(1, "http://new_img.jpg", "Attack on Titan Final",
                Status.COMPLETED, 25, 25);

        animeService.updateAnime(updatedAnime);

        verify(animeDao, times(1)).updateAnime(updatedAnime);
        assertEquals(Status.COMPLETED, updatedAnime.getStatus());
    }

    @Test
    void testDeleteAnime() throws Exception {
        animeService.deleteAnime(1);

        verify(animeDao, times(1)).deleteAnime(1);
    }

    @Test
    void testGetAnimesByStatus() {
        when(animeDao.getAllAnimes()).thenReturn(Arrays.asList(anime1, anime2, anime3));

        List<Anime> watchingAnime = animeService.getAnimesByStatus(Status.WATCHING);
        List<Anime> completedAnime = animeService.getAnimesByStatus(Status.COMPLETED);

        assertEquals(1, watchingAnime.size());
        assertEquals("Attack on Titan", watchingAnime.get(0).getTitle());

        assertEquals(1, completedAnime.size());
        assertEquals("Death Note", completedAnime.get(0).getTitle());
    }

    @Test
    void testValidateAnime_Success() throws Exception {
        Anime validAnime = new Anime(1, "http://valid.jpg", "Valid Anime",
                Status.WATCHING, 10, 5);

        assertDoesNotThrow(() -> animeService.validateAnime(validAnime));
    }

    @Test
    void testValidateAnime_InvalidTitle() throws Exception {
        Anime invalidAnime = new Anime(1, "http://valid.jpg", "",
                Status.WATCHING, 10, 5);

        Exception exception = assertThrows(Exception.class,
                () -> animeService.validateAnime(invalidAnime));
        assertEquals("Название аниме не может быть пустым", exception.getMessage());
    }

    @Test
    void testValidateAnime_InvalidEpisodes() throws Exception {
        Anime invalidAnime = new Anime(1, "http://valid.jpg", "Invalid Anime",
                Status.WATCHING, 10, 20);

        Exception exception = assertThrows(Exception.class,
                () -> animeService.validateAnime(invalidAnime));
        assertEquals("Некорректное количество просмотренных серий", exception.getMessage());
    }

    @Test
    void testAutoCompleteStatus() throws Exception {
        when(animeDao.getAnimeById(4)).thenReturn(new Anime(4, "http://img4.jpg", "Naruto",
                Status.WATCHING, 220, 50));
        Anime anime = new Anime(4, "http://img4.jpg", "Short Anime",
                Status.WATCHING, 5, 5);

        animeService.updateAnime(anime);

        assertEquals(Status.COMPLETED, anime.getStatus());
        verify(animeDao, times(1)).updateAnime(anime);
    }

    @Test
    void testGetAnimeByStatus() {
        when(animeDao.getAllAnimes()).thenReturn(Arrays.asList(anime1, anime2, anime3));

        List<Anime> result = animeService.getAnimesByStatus(Status.COMPLETED);

        assertEquals(1, result.size());
        assertEquals("Death Note", result.get(0).getTitle());
    }
}