package org.bouncycastle.tls;

class Timeout
{
    private long durationMillis;
    private long startMillis;

    Timeout(long durationMillis)
    {
        this(durationMillis, System.currentTimeMillis());
    }

    Timeout(long durationMillis, long currentTimeMillis)
    {
        this.durationMillis = Math.max(0, durationMillis);
        this.startMillis = Math.max(0, currentTimeMillis);
    }

    long remainingMillis()
    {
        return remainingMillis(System.currentTimeMillis());
    }

    synchronized long remainingMillis(long currentTimeMillis)
    {
        // If clock jumped backwards, reset start time 
        if (startMillis > currentTimeMillis)
        {
            startMillis = currentTimeMillis;
            return durationMillis;
        }

        long elapsed = currentTimeMillis - startMillis;
        long remaining = durationMillis - elapsed;

        // Once timeout reached, lock it in
        if (remaining <= 0)
        {
            return durationMillis = 0L;
        }

        return remaining;
    }

    static Timeout forWaitMillis(int waitMillis)
    {
        return forWaitMillis(waitMillis, System.currentTimeMillis());
    }

    static Timeout forWaitMillis(int waitMillis, long currentTimeMillis)
    {
        if (waitMillis < 0)
        {
            throw new IllegalArgumentException("'waitMillis' cannot be negative");
        }
        if (waitMillis > 0)
        {
            return new Timeout(waitMillis, currentTimeMillis);
        }
        return null;
    }

    static int getWaitMillis(Timeout timeout)
    {
        return getWaitMillis(timeout, System.currentTimeMillis());
    }

    static int getWaitMillis(Timeout timeout, long currentTimeMillis)
    {
        if (null == timeout)
        {
            return 0;
        }
        long remainingMillis = timeout.remainingMillis(currentTimeMillis);
        if (remainingMillis < 1L)
        {
            return -1;
        }
        if (remainingMillis > Integer.MAX_VALUE)
        {
            return Integer.MAX_VALUE;
        }
        return (int)remainingMillis;
    }
}
