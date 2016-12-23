<?php

namespace Application\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Notification
 *
 * @ORM\Table(name="notification", indexes={@ORM\Index(name="fk_notification_idx", columns={"patientId"})})
 * @ORM\Entity
 */
class Notification
{
    /**
     * @var integer
     *
     * @ORM\Column(name="id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var string
     *
     * @ORM\Column(name="text", type="text", length=65535, nullable=false)
     */
    private $text;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="datetime", type="datetime", nullable=false)
     */
    private $datetime;

    /**
     * @var \Application\Entity\Patient
     *
     * @ORM\ManyToOne(targetEntity="Application\Entity\Patient")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="patientId", referencedColumnName="id")
     * })
     */
    private $patientid;



    /**
     * Get id
     *
     * @return integer
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set text
     *
     * @param string $text
     *
     * @return Notification
     */
    public function setText($text)
    {
        $this->text = $text;

        return $this;
    }

    /**
     * Get text
     *
     * @return string
     */
    public function getText()
    {
        return $this->text;
    }

    /**
     * Set datetime
     *
     * @param \DateTime $datetime
     *
     * @return Notification
     */
    public function setDatetime($datetime)
    {
        $this->datetime = $datetime;

        return $this;
    }

    /**
     * Get datetime
     *
     * @return \DateTime
     */
    public function getDatetime()
    {
        return $this->datetime;
    }

    /**
     * Set patientid
     *
     * @param \Application\Entity\Patient $patientid
     *
     * @return Notification
     */
    public function setPatientid(\Application\Entity\Patient $patientid = null)
    {
        $this->patientid = $patientid;

        return $this;
    }

    /**
     * Get patientid
     *
     * @return \Application\Entity\Patient
     */
    public function getPatientid()
    {
        return $this->patientid;
    }
}
