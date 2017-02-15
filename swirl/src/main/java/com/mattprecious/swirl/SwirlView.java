package com.mattprecious.swirl;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.M)
public final class SwirlView extends ImageView {
  // Keep in sync with attrs.
  public enum State {
    OFF,
    ON,
    ERROR,
  }

  Integer errorColor;
  Integer onColor;

  private State state = State.OFF;

  public SwirlView(Context context) {
    this(context, null);
  }

  public SwirlView(Context context, AttributeSet attrs) {
    super(context, attrs);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      throw new AssertionError("API 23 required.");
    }

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.swirl_Swirl);
    int state = a.getInteger(R.styleable.swirl_Swirl_swirl_state, -1);
    if (state != -1) {
      setState(State.values()[state], false);
    }
    a.recycle();
  }
  public void setErrorIconColor(int color) {
    this.errorColor = color;
  }

  public void setSwirlIconColor(int color) {
    this.onColor = color;
  }

  public void resetColors() {
    this.errorColor = null;
    this.onColor = null;
  }

  public void setState(State state) {
    setState(state, true);
  }

  public void setState(State state, boolean animate) {
    if (state == this.state) return;

    @DrawableRes int resId = getDrawable(this.state, state, animate);
    if (resId == 0) {
      setImageResource(resId);
    } else {
      Drawable icon = getContext().getDrawable(resId);
      if (state == State.ERROR && icon != null && errorColor != null) {
        Drawable mutatedIcon = DrawableCompat.wrap(icon).mutate();
        DrawableCompat.setTint(mutatedIcon, errorColor);
        icon = DrawableCompat.unwrap(mutatedIcon);
      } else if (state == State.ON && icon != null && onColor != null) {
        Drawable mutatedIcon = DrawableCompat.wrap(icon).mutate();
        DrawableCompat.setTint(mutatedIcon, onColor);
        icon = DrawableCompat.unwrap(mutatedIcon);
      }
      setImageDrawable(icon);

      if (icon instanceof AnimatedVectorDrawable) {
        ((AnimatedVectorDrawable) icon).start();
      }
    }

    this.state = state;
  }

  @DrawableRes private static int getDrawable(State currentState, State newState, boolean animate) {
    switch (newState) {
      case OFF:
        if (animate) {
          if (currentState == State.ON) {
            return R.drawable.swirl_draw_off_animation;
          } else if (currentState == State.ERROR) {
            return R.drawable.swirl_error_off_animation;
          }
        }

        return 0;
      case ON:
        if (animate) {
          if (currentState == State.OFF) {
            return R.drawable.swirl_draw_on_animation;
          } else if (currentState == State.ERROR) {
            return R.drawable.swirl_error_state_to_fp_animation;
          }
        }

        return R.drawable.swirl_fingerprint;
      case ERROR:
        if (animate) {
          if (currentState == State.ON) {
            return R.drawable.swirl_fp_to_error_state_animation;
          } else if (currentState == State.OFF) {
            return R.drawable.swirl_error_on_animation;
          }
        }

        return R.drawable.swirl_error;
      default:
        throw new IllegalArgumentException("Unknown state: " + newState);
    }
  }
}
