package phan.blog.model;

import com.github.slugify.Slugify;

import java.io.IOException;
import java.util.*;

public class BlogEntry {
    private String slug;
    private String title;
    private String content;
    private Date date;
    private String space = " ";
    private Set<BlogComments> comment = new HashSet<>();
    private List<Tag> tags = new ArrayList<>();

    public BlogEntry(String title, String content, Date date) {
        this.title = title;
        this.content = content;
        this.date = date;

        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<BlogComments> getComment() {
        return comment;
    }

    public void setComment(Set<BlogComments> comment) {
        this.comment = comment;
    }

    public boolean addComment(BlogComments comment) {
        // Store these comments!
        return this.comment.add(comment);
    }

    public boolean addTags(Tag tag) {
        return this.tags.add(tag);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(String tag) {
        List<Tag> newTags = new ArrayList<>();
        String delims = "[ ]+";
        String[] tags = tag.split(delims);
        int tagIndex = 1;

        for (String eachTag : tags) {
            if (eachTag.trim().equals("")) {
                continue;
            } else {
                newTags.add(new Tag(eachTag));
                tagIndex++;
            }
        }

        this.tags = newTags;
    }

    public void addTags2(String tag) {
        String delims = "[ ]+";
        String[] tags = tag.split(delims);
        int tagIndex = 1;

        for (String eachTag : tags) {
            this.tags.add(new Tag(eachTag));
            tagIndex++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogEntry blogEntry = (BlogEntry) o;

        if (title != null ? !title.equals(blogEntry.title) : blogEntry.title != null) return false;
        if (content != null ? !content.equals(blogEntry.content) : blogEntry.content != null) return false;
        if (date != null ? !date.equals(blogEntry.date) : blogEntry.date != null) return false;
        return comment != null ? comment.equals(blogEntry.comment) : blogEntry.comment == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

}
