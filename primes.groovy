import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ConcurrentHashMap

boolean isPrime(BigInteger n) {
    return n > 0 && n.isProbablePrime(60)
}

fCache = new ConcurrentHashMap()

BigInteger f(int n) {
    def cached = fCache.get(n)
    if (cached != null) {
        return cached
    }

    BigInteger t = 1
    for (int i = 1; i <= n; i++) {
        if (!isPrime(i)) {
            continue
        }
        t *= i
    }
    fCache.put(n, t)
    return t
}

AtomicInteger num = new AtomicInteger(8)

resultMap = new ConcurrentHashMap()

MAX = 100000

List<Thread> threadList = new ArrayList<>()
for (int i = 0; i < 8; i++) {
    Thread thread = new Thread({
        while (true) {
            if (resultMap.size() > 1000) {
                Thread.sleep(15)
            }
            int n = num.getAndAdd(2)
            if (n > MAX) {
                break
            }
            BigInteger t = f((int) Math.sqrt(n))

            BigInteger t2 = t + n
            BigInteger to = n - 3
            int count = 0
            for (BigInteger k = 3; k <= to; k += 2) {
                if (isPrime(k) && isPrime(t2 - k)) {
                    count++
                }
            }
            resultMap.put(n, count)
        }
    })
    threadList.add(thread)
}

threadList.forEach { it -> it.start() }

for (int i = 8; i <= MAX; i += 2) {
    Object res = null
    while (true) {
        res = resultMap.remove(i)
        if (res != null) {
            break
        }
        Thread.sleep(15)
    }

    println "${i} ${res}"
}

threadList.forEach { it -> it.join() }
