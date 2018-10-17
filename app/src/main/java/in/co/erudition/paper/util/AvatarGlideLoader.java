package in.co.erudition.paper.util;

import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import in.co.erudition.avatar.AvatarLoaderBase;
import in.co.erudition.avatar.AvatarPlaceholder;
import in.co.erudition.avatar.AvatarView;
import in.co.erudition.paper.R;

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
        Glide
                .with(avatarView.getContext())
                .load(avatarUrl)
                .apply(RequestOptions.placeholderOf(avatarPlaceholder).fitCenter())
                .transition(withCrossFade())
                .into(avatarView);
    }
}
