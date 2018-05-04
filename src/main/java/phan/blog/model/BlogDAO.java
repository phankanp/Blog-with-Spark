package phan.blog.model;

import java.util.List;

public interface BlogDAO {
    boolean addEntry(BlogEntry blogEntry);

    boolean removeEntry(BlogEntry blogEntry);

    List<BlogEntry> findAllEntries();

    BlogEntry findEntryBySlug(String slug);
}
