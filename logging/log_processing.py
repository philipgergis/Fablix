import sys

if __name__ == "__main__":
    ts = []
    tj = []
    with open(sys.argv[1]) as f:
        lines = f.readlines()
        for l in lines:
            times = l.split(',')
            listTS = times[0].split('-')
            numTS = int(listTS[1])
            listTJ = times[1].split('-')
            numTJ = int(listTJ[1].strip("\n"))
            ts.append(numTS)
            tj.append(numTJ)
    print("TS: ", (sum(ts)/len(ts))/(10**6), "| TJ: ", (sum(tj)/len(tj))/(10**6))
