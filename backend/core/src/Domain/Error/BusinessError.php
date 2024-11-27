<?php

namespace Core\Domain\Error;

/**
 * @template T
 */
readonly class BusinessError
{
    /**
     * @param string $message
     * @param T $context
     */
    public function __construct(
        public string $message,
        public mixed  $context = null
    ) {}
}