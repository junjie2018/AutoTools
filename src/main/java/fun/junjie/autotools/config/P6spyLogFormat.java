package fun.junjie.autotools.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import io.micrometer.core.instrument.util.StringUtils;

public class P6spyLogFormat implements MessageFormattingStrategy {

    @Override
    public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url) {

        return StringUtils.isNotEmpty(sql) ? new StringBuilder().append(" Execute SQL：>>>>").append(sql.replaceAll("[\\s]+", " ")).append(String.format(" <<<< Execute time: %s ms", elapsed)).toString() : null;
    }
}
