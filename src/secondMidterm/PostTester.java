package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

abstract class Component{
    String username;
    String postContent;
    List<Comment>directComments;

    public Component(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
        this.directComments=new ArrayList<>();
    }
}
class Comment extends Component{
    int likes;

    public Comment(String username, String postContent) {
        super(username, postContent);
        this.likes = 0;
    }
    public void like(){
        likes++;
    }
    public void addReply(Comment comment){
        directComments.add(comment);
    }
    public int likeCount(){
        if (directComments.isEmpty())return likes;
        return likes + directComments.stream().mapToInt(Comment::likeCount).sum();
    }
    public String print(int indend){
        StringBuilder sb = new StringBuilder();
        String tabs = IntStream.range(0,indend).mapToObj(i->" ").collect(Collectors.joining(""));
        sb.append(String.format("%sComment: %s\n%sWritten by: %s\n%sLikes: %d\n",tabs,postContent,tabs,username,tabs,likes));
        directComments.stream()
                .sorted(Comparator.comparing(Comment::likeCount).reversed())
                .forEach(i->sb.append(i.print(indend+4)));
        return sb.toString();
    }
}
class Post extends Component{
   Map<String,Comment>comments;

    public Post(String username, String postContent) {
        super(username, postContent);
        this.comments = new HashMap<>();
    }

    public void addComment(String username, String commentId, String content, String replyToId){
        Comment comment = new Comment(username,content);
        comments.putIfAbsent(commentId,comment);
        if (replyToId != null)
            comments.get(replyToId).addReply(comment);
        else
            directComments.add(comment);
    }
    public void likeComment(String commentId){
        comments.get(commentId).like();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Post: %s\nWritten by: %s\nComments:\n",postContent,username));
        directComments.stream()
                .sorted(Comparator.comparing(Comment::likeCount).reversed())
                .forEach(i->sb.append(i.print(8)));
        return sb.toString();
    }
}

public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}

