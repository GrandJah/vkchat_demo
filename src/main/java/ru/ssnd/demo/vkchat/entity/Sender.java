package ru.ssnd.demo.vkchat.entity;

/**
 * @author Igor Kovalkov
 * @link http://ik-net.ru
 * 06.05.2018
 */
public class Sender {
    private Long id;
    private String avatarUrl;
    private String name;

    public Sender(Long id, String avatar, String name) {
        this.id = id;
        this.avatarUrl = avatar;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }
}
