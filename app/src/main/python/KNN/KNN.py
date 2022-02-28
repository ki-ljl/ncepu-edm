from os.path import dirname, join
import numpy as np
from joblib import dump, load




def test(model, list, row, col):
    filename = join(dirname(__file__), model)
    # print(filename)
    clf = load(filename)
    res = clf.predict(np.array(list).reshape(row, col))
    print(res)
    return res