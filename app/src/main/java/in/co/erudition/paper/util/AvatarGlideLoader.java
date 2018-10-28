package in.co.erudition.paper.util;

import android.support.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;

import in.co.erudition.avatar.AvatarLoaderBase;
import in.co.erudition.avatar.AvatarPlaceholder;
import in.co.erudition.avatar.AvatarView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AvatarGlideLoader extends AvatarLoaderBase {

    public AvatarGlideLoader() {
        super();
    }

    public AvatarGlideLoader(String defaultPlaceholderString) {
        super(defaultPlaceholderString);
    }

    @Override
    public void loadImage(@NonNull AvatarView avatarView, @NonNull AvatarPlaceholder avatarPlaceholder, String avatarUrl) {
        GlideApp
                .with(avatarView.getContext())
                .load(avatarUrl)
                .apply(RequestOptions.placeholderOf(avatarPlaceholder).fitCenter())
                .error(avatarPlaceholder)
                .transition(withCrossFade())
                .into(avatarView);
    }
}
