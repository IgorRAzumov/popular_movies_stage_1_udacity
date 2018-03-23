package ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;


public abstract class BaseAsyncTaskLoader<T> extends AsyncTaskLoader<T> {
    private T result;

    public BaseAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public abstract T loadInBackground();


    public void startLoad() {
        if (result != null) {
            deliverResult(result);
        } else {
            forceLoad();
        }

    }

    @Override
    public void deliverResult(T result) {
        this.result = result;
        super.deliverResult(result);
    }
}

