import csv
from pathlib import Path
import matplotlib.pyplot as plt

folder = Path(__file__).resolve().parent / 'results'
with (folder / 'timings.csv').open() as f:
    data = list(csv.DictReader(f))

def values(algorithm, case):
    rows = [r for r in data if r['algorithm'] == algorithm and r['case'] == case]
    return [int(r['size']) for r in rows], [float(r['average_ms']) for r in rows]

fig, ax = plt.subplots(figsize=(7, 4))
for name in ('LCS', 'EditDistance'):
    x, y = values(name, 'random')
    ax.plot(x, y, marker='o', label='Edit Distance' if name == 'EditDistance' else name)
ax.set(xlabel='Length of each string', ylabel='Average time (ms)', title='Dynamic programming')
ax.grid(alpha=.25)
ax.legend()
fig.tight_layout()
fig.savefig(folder / 'dp_plot.png', dpi=170)
plt.close(fig)

fig, axes = plt.subplots(1, 2, figsize=(11, 4), sharey=True)
for ax, case in zip(axes, ('present', 'absent')):
    for name in ('Naive', 'KMP', 'RabinKarp'):
        x, y = values(name, case)
        ax.plot(x, y, marker='o', label='Rabin–Karp' if name == 'RabinKarp' else name)
    ax.set(xlabel='Text length', title=f'Pattern {case}')
    ax.tick_params(axis='x', labelrotation=25)
    ax.grid(alpha=.25)
axes[0].set_ylabel('Average time (ms)')
axes[1].legend()
fig.tight_layout()
fig.savefig(folder / 'matching_plot.png', dpi=170)
