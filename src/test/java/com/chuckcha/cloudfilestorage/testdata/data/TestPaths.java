package com.chuckcha.cloudfilestorage.testdata.data;

import com.chuckcha.cloudfilestorage.dto.request.path.AnyPathDto;
import com.chuckcha.cloudfilestorage.dto.request.path.DirectoryPathDto;
import com.chuckcha.cloudfilestorage.dto.request.SearchRequest;

import java.util.stream.Stream;

public class TestPaths {

    // ======= VALID DirectoryPathDto (только директории, оканчиваются на /) =======
    public static final DirectoryPathDto FOLDER_SIMPLE = new DirectoryPathDto("folder/");
    public static final DirectoryPathDto FOLDER_NESTED = new DirectoryPathDto("folder/subfolder/inner-folder/");
    public static final DirectoryPathDto FOLDER_WITH_DASH = new DirectoryPathDto("folder-name/");
    public static final DirectoryPathDto FOLDER_WITH_UNDERSCORE = new DirectoryPathDto("folder_name/");
    public static final DirectoryPathDto MULTI_NESTED_FOLDER = new DirectoryPathDto("a/b/c/d/");

    public static final DirectoryPathDto FOLDER_WITH_MOVED_SUBFOLDER = new DirectoryPathDto("folder/inner-folder/");
    public static final DirectoryPathDto FOLDER_WITH_RENAMED_SUBFOLDER = new DirectoryPathDto("folder/subfolder/inner-folder-new/");
    public static final DirectoryPathDto FOLDER_WITH_RENAMED_AND_MOVED_SUBFOLDER = new DirectoryPathDto("folder/inner-folder-new/");

    // ======= INVALID DirectoryPathDto =======
    public static final DirectoryPathDto NO_TRAILING_SLASH = new DirectoryPathDto("folder");
    public static final DirectoryPathDto INVALID_CHARS_FOLDER = new DirectoryPathDto("fo*lder/");
    public static final DirectoryPathDto DOUBLE_SLASH_FOLDER = new DirectoryPathDto("folder//sub/");
    public static final DirectoryPathDto LEADING_SLASH_FOLDER = new DirectoryPathDto("/folder/");
    public static final DirectoryPathDto WHITESPACE_FOLDER = new DirectoryPathDto("  /");
    public static final DirectoryPathDto TRAILING_SPACE_FOLDER = new DirectoryPathDto("folder /");

    // ======= VALID AnyPathDto (файлы и папки) =======
    public static final AnyPathDto FILE_IN_ROOT = new AnyPathDto("file.txt");
    public static final AnyPathDto FILE_IN_FOLDER = new AnyPathDto("folder/file.txt");
    public static final AnyPathDto FILE_IN_DEEP_FOLDER = new AnyPathDto("a/b/c/file.json");
    public static final AnyPathDto FILE_WITH_DASH = new AnyPathDto("folder/file-name.yml");
    public static final AnyPathDto FILE_WITH_UNDERSCORE = new AnyPathDto("folder/config_file.yml");
    public static final AnyPathDto FILE_WITHOUT_EXTENSION = new AnyPathDto("folder/file");
    public static final AnyPathDto HIDDEN_FILE = new AnyPathDto("folder/.env");
    public static final AnyPathDto FOLDER_AS_PATH = new AnyPathDto("folder/");

    // ======= INVALID AnyPathDto =======
    public static final AnyPathDto INVALID_SYMBOLS_FILE = new AnyPathDto("folder/file<>.txt");
    public static final AnyPathDto DOUBLE_SLASH_FILE = new AnyPathDto("folder//file.txt");
    public static final AnyPathDto LEADING_SLASH_FILE = new AnyPathDto("/folder/file.txt");
    public static final AnyPathDto WHITESPACE_FILE = new AnyPathDto("folder/  ");
    public static final AnyPathDto FILE_WITH_SPACES = new AnyPathDto("folder/my file.txt");
    public static final AnyPathDto ONLY_SLASHES = new AnyPathDto("///");

    // ======= VALID SearchRequest (до 100 символов, путь или текст) =======
    public static final SearchRequest SIMPLE_SEARCH = new SearchRequest("file");
    public static final SearchRequest NESTED_SEARCH = new SearchRequest("folder1/folder2/file.txt");
    public static final SearchRequest SEARCH_WITH_UNDERSCORE = new SearchRequest("user_files/data_2023.json");
    public static final SearchRequest MAX_LENGTH_SEARCH = new SearchRequest("a".repeat(100));

    // ======= INVALID SearchRequest =======
    public static final SearchRequest EMPTY_SEARCH = new SearchRequest("");
    public static final SearchRequest SEARCH_WITH_INVALID_CHARS = new SearchRequest("file<>name");
    public static final SearchRequest SEARCH_WITH_SPACES = new SearchRequest("file with space.txt");

    // ======= Stream-помощники =======

    public static Stream<DirectoryPathDto> validDirectoryPaths() {
        return Stream.of(
                FOLDER_SIMPLE,
                FOLDER_NESTED,
                FOLDER_WITH_DASH,
                FOLDER_WITH_UNDERSCORE,
                MULTI_NESTED_FOLDER
        );
    }

    public static Stream<DirectoryPathDto> invalidDirectoryPaths() {
        return Stream.of(
                NO_TRAILING_SLASH,
                INVALID_CHARS_FOLDER,
                DOUBLE_SLASH_FOLDER,
                LEADING_SLASH_FOLDER,
                WHITESPACE_FOLDER,
                TRAILING_SPACE_FOLDER
        );
    }

    public static Stream<AnyPathDto> validAnyPaths() {
        return Stream.of(
                FILE_IN_ROOT,
                FILE_IN_FOLDER,
                FILE_IN_DEEP_FOLDER,
                FILE_WITH_DASH,
                FILE_WITH_UNDERSCORE,
                FILE_WITHOUT_EXTENSION,
                HIDDEN_FILE,
                FOLDER_AS_PATH
        );
    }

    public static Stream<AnyPathDto> invalidAnyPaths() {
        return Stream.of(
                INVALID_SYMBOLS_FILE,
                DOUBLE_SLASH_FILE,
                LEADING_SLASH_FILE,
                WHITESPACE_FILE,
                FILE_WITH_SPACES,
                ONLY_SLASHES
        );
    }

    public static Stream<SearchRequest> validSearchRequests() {
        return Stream.of(
                SIMPLE_SEARCH,
                NESTED_SEARCH,
                SEARCH_WITH_UNDERSCORE,
                MAX_LENGTH_SEARCH
        );
    }

    public static Stream<SearchRequest> invalidSearchRequests() {
        return Stream.of(
                EMPTY_SEARCH,
                SEARCH_WITH_INVALID_CHARS,
                SEARCH_WITH_SPACES
        );
    }

    public static Stream<AnyPathDto> validFileAndDirectoryPaths() {
        return Stream.of(
                FOLDER_AS_PATH,
                FILE_IN_ROOT
        );
    }
}