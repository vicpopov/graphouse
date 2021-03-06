package ru.yandex.market.graphouse.search.tree;

import com.google.common.cache.LoadingCache;
import ru.yandex.market.graphouse.search.MetricStatus;

import java.util.Map;

/**
 * @author Dmitry Andreev <a href="mailto:AndreevDm@yandex-team.ru"></a>
 * @date 25/01/2017
 */
public class LoadableMetricDir extends MetricDir {
    private final LoadingCache<MetricDir, DirContent> dirContentProvider;

    public LoadableMetricDir(MetricDir parent, String name, MetricStatus status,
                             LoadingCache<MetricDir, DirContent> dirContentProvider) {
        super(parent, name, status);
        this.dirContentProvider = dirContentProvider;
    }

    private DirContent getContent() {
        return dirContentProvider.getUnchecked(this);
    }

    private DirContent getContentOrEmpty() {
        DirContent content = dirContentProvider.getIfPresent(this);
        return (content == null) ? DirContent.EMPTY : content;
    }

    @Override
    public Map<String, MetricDir> getDirs() {
        return getContent().getDirs();
    }

    @Override
    public Map<String, MetricName> getMetrics() {
        return getContent().getMetrics();
    }

    @Override
    public boolean hasDirs() {
        return !getDirs().isEmpty();
    }

    @Override
    public boolean hasMetrics() {
        return !getMetrics().isEmpty();
    }

    @Override
    public MetricDir maybeGetDir(String name) {
        return getContentOrEmpty().getDirs().get(name);
    }

    @Override
    public MetricName maybeGetMetric(String name) {
        return getContentOrEmpty().getMetrics().get(name);
    }

    @Override
    public int loadedMetricCount() {
        DirContent dirContent = getContentOrEmpty();
        int count = dirContent.getMetrics().size();
        for (MetricDir metricDir : dirContent.getDirs().values()) {
            count += metricDir.loadedMetricCount();
        }
        return count;
    }

    @Override
    public int loadedDirCount() {
        DirContent dirContent = getContentOrEmpty();
        int count = dirContent.getDirs().size();
        for (MetricDir metricDir : dirContent.getDirs().values()) {
            count += metricDir.loadedDirCount();
        }
        return count;
    }


}
