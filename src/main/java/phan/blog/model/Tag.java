package phan.blog.model;

import com.github.slugify.Slugify;

import java.io.IOException;

public class Tag {
    private String tag;
    private String slug;

    public Tag(String tag) {
        this.tag = tag;

        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tag='" + tag + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
