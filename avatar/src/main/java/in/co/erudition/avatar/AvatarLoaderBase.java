package in.co.erudition.avatar;

import android.support.annotation.NonNull;

public abstract class AvatarLoaderBase implements AvatarLoader {

    private String defaultPlaceholderString;

    public AvatarLoaderBase() {
        this.defaultPlaceholderString = AvatarPlaceholder.DEFAULT_PLACEHOLDER_STRING;
    }

    public AvatarLoaderBase(String defaultPlaceholderString) {
        this.defaultPlaceholderString = defaultPlaceholderString;
    }

    @Override
    public void loadImage(@NonNull AvatarView avatarView, String avatarUrl, String name) {
        loadImage(avatarView, new AvatarPlaceholder(name, defaultPlaceholderString), avatarUrl);
    }

    @Override
    public void loadImage(@NonNull AvatarView avatarView, String avatarUrl, String name, int textSizePercentage) {
        loadImage(avatarView, new AvatarPlaceholder(name, textSizePercentage, defaultPlaceholderString), avatarUrl);
    }
}
