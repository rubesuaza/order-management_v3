"""
Fixtures globales de pytest.
"""

import sys
from pathlib import Path

# Añadir src al path para importaciones
src_path = Path(__file__).resolve().parent.parent / "src"
if str(src_path) not in sys.path:
    sys.path.insert(0, str(src_path))
