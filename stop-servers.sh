#!/bin/bash
echo "Stopping MoneyMate servers..."
fuser -k 8080/tcp 2>/dev/null
fuser -k 3000/tcp 2>/dev/null
echo "✓ All servers stopped"
