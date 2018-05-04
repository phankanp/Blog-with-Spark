package phan.blog;


import phan.blog.model.*;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.*;

import static spark.Spark.*;


public class Main {
    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {
        staticFileLocation("/css");

        BlogDAO dao = new BlogEntryDAO();

        // -----------------Before-------------------------

        before((req, res) -> {
            if (req.cookie("password") != null) {
                req.attribute("password", req.cookie("password"));
            }
        });

        before("/new-entry", (req, res) -> {
            if (req.attribute("password") == null) {
                setFlashMessage(req, "Please login fist");
                res.redirect("/password");
                halt();
            }
        });

        before("/edit/:slug", (req, res) -> {
            if (req.attribute("password") == null) {
                setFlashMessage(req, "Please login fist");
                res.redirect("/password");
                halt();
            }
        });

        // -----------------Index-------------------------

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("tag/:slug", (req, res) -> {
            List<BlogEntry> allEntries = dao.findAllEntries();
            List<BlogEntry> tagEntries = new ArrayList<>();
            for (BlogEntry entry : allEntries) {
                for (Tag tag : entry.getTags()) {
                    if (tag.getTag().equals(req.params("slug"))) {
                        tagEntries.add(entry);
                    }
                }
            }
            System.out.print(tagEntries);
            Map<String, Object> model = new HashMap<>();
            model.put("tagEntries", tagEntries);
            return new ModelAndView(model, "tag.hbs");
        }, new HandlebarsTemplateEngine());

        // -----------------Post Details-------------------------

        get("/details/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "details.hbs");
        }, new HandlebarsTemplateEngine());

        post("details/:slug", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            blogEntry.addComment(new BlogComments(req.queryParams("name"), req.queryParams("comment"), new Date()));
            res.redirect("/details/" + blogEntry.getSlug());
            return null;
        });

        // -----------------Delete Post-------------------------

        get("delete/:slug", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            dao.removeEntry(blogEntry);
            res.redirect("/");

            return null;
        });

        // -----------------New Post-------------------------

        get("/new-entry", (req, res) -> {
            return new ModelAndView(null, "new.hbs");
        }, new HandlebarsTemplateEngine());

        post("/new-post", (req, res) -> {
            BlogEntry blogEntry = new BlogEntry(req.queryParams("title"),
                    req.queryParams("entry"),
                    new Date());
            if(req.queryParams("tags").trim().equals("")) {
                dao.addEntry(blogEntry);
            } else {
                blogEntry.addTags2(req.queryParams("tags"));
                dao.addEntry(blogEntry);
            }
            res.redirect("/");
            return null;
        });

        // -----------------Editing Post-------------------------

        get("/edit/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("edit", dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/edited-post/:slug", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug((req.params("slug")));
            blogEntry.setTitle(req.queryParams("title"));
            blogEntry.setContent(req.queryParams("entry"));
            blogEntry.setTags(req.queryParams("tags"));
            res.redirect("/details/" + blogEntry.getSlug());
            return null;
        });

        // -----------------Password page-------------------------

        get("/password", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/password", (req, res) -> {
            String password = req.queryParams("password");
            if (password.toLowerCase().equals("admin")) {
                res.cookie("password", password);
                res.redirect("/");
            } else {
                setFlashMessage(req, "Invalid user name, please try again");
                res.redirect("/password");
            }

            return null;
        });

        // -----------------Page not found-------------------------

        exception(NotFoundException.class, (exc, req, res) -> {
            res.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(
                    new ModelAndView(null, "not-found.hbs"));
            res.body(html);
        });

        // -----------------Test blog entries-------------------------

        BlogEntry entry1;
        dao.addEntry(entry1 = new BlogEntry("Test Blog Entry 1", "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Morbi pharetra, arcu in porta dictum, justo justo gravida dui, in posuere erat nisl nec arcu. " +
                "Curabitur luctus semper turpis nec tristique. Maecenas id sodales libero. Donec condimentum, dolor " +
                "id placerat auctor, libero lectus blandit dui, gravida sodales ex sapien id velit. Mauris ac nunc " +
                "ante. Suspendisse quis tempus urna. Donec condimentum condimentum turpis, nec facilisis lorem auctor" +
                " non. Quisque aliquet diam sit amet porttitor maximus. Sed tristique mollis aliquam. Aenean mattis, " +
                "elit ut tempor viverra, erat enim auctor est, in sodales neque orci ac dolor. Cras convallis " +
                "venenatis neque quis tempus. In pretium dictum mauris, eu sollicitudin dui facilisis sed. Nunc " +
                "fermentum quam a justo maximus pretium. Maecenas orci quam, mattis nec porta posuere, vulputate in " +
                "dolor. Vivamus dui magna, fermentum gravida dignissim ac, viverra quis augue. Praesent arcu eros, " +
                "lobortis vel odio et, bibendum finibus nisl.", new Date()));
        entry1.addComment(new BlogComments("Test commenter 1", "test comment 1", new Date()));
        entry1.addTags(new Tag("tag1"));
        entry1.addTags(new Tag("tag2"));
        entry1.addTags(new Tag("tag3"));

        BlogEntry entry2;
        dao.addEntry(entry2 = new BlogEntry("Test Blog Entry 2", "Nulla ut volutpat ante. Nunc tristique purus vel tellus " +
                "commodo, id ullamcorper elit rhoncus. Vestibulum eleifend augue quis eros porttitor feugiat. Integer" +
                " fermentum euismod augue vitae fringilla. Morbi ac convallis erat. Nunc at consequat libero. In quis" +
                " rutrum sem. Nullam id rhoncus risus, ac consequat elit. Fusce ullamcorper, sem eget ornare sodales," +
                " sem orci consequat nunc, eget tincidunt purus eros eget enim. Cras at mauris sed lorem tristique " +
                "hendrerit vel vitae nulla. Quisque ac nulla fermentum, dignissim arcu in, sagittis ligula. Integer " +
                "tortor urna, cursus ac tortor quis, egestas placerat metus. Fusce finibus diam vitae nulla iaculis " +
                "fringilla. Curabitur eget turpis laoreet, aliquam dui sit amet, lobortis massa. Nunc neque enim, " +
                "tempus eget dapibus id, cursus vitae justo. Maecenas mi neque, consectetur quis est at, porttitor " +
                "pulvinar neque.", new Date()));
        entry2.addComment(new BlogComments("Test commenter 2", "test comment 2", new Date()));
        entry2.addTags(new Tag("tag3"));
        entry2.addTags(new Tag("tag4"));

        BlogEntry entry3;
        dao.addEntry(entry3 = new BlogEntry("Test Blog Entry 3", "Suspendisse nec feugiat dui. Etiam auctor dolor in justo" +
                " interdum cursus. Ut semper tristique volutpat. Sed sapien augue, imperdiet nec lacinia non, mattis " +
                "in neque. In nunc ex, aliquet sit amet aliquam in, scelerisque in massa. Cras suscipit, turpis eu " +
                "ornare auctor, nisl est convallis urna, sit amet facilisis eros neque eu massa. Vestibulum rhoncus " +
                "bibendum nisl ac accumsan. Aliquam fringilla ornare justo maximus ultricies. Lorem ipsum dolor sit " +
                "amet, consectetur adipiscing elit. Donec id cursus neque. Nullam et pharetra dolor, a facilisis " +
                "lacus. Nulla sed quam sem.", new Date()));
        entry3.addComment(new BlogComments("Test commenter 3", "test comment 3", new Date()));
        entry3.addTags(new Tag("tag1"));
        entry3.addTags(new Tag("tag2"));
    }

    // -----------------Flash Message Methods-------------------------

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        if (req.session(false) == null) {
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if (message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }
}
