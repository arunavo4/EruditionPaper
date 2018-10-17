package in.co.erudition.avatar;

import android.support.annotation.NonNull;

public interface AvatarLoader {
    void loadImage(@NonNull AvatarView avatarView, @NonNull AvatarPlaceholder avatarPlaceholder, String avatarUrl);

    void loadImage(@NonNull AvatarView avatarView, String avatarUrl, String name);

    void loadImage(@NonNull AvatarView avatarView, String avatarUrl, String name, int textSizePercentage);
}
