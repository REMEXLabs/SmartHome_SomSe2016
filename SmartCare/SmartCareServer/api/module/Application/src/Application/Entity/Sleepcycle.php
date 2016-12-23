<?php

namespace Application\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Sleepcycle
 *
 * @ORM\Table(name="sleepcycle", indexes={@ORM\Index(name="fk_patient_sc_idx", columns={"patientId"})})
 * @ORM\Entity
 */
class Sleepcycle
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
     * @var \DateTime
     *
     * @ORM\Column(name="dateFrom", type="datetime", nullable=false)
     */
    private $datefrom;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="dateTo", type="datetime", nullable=true)
     */
    private $dateto;

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
     * Set datefrom
     *
     * @param \DateTime $datefrom
     *
     * @return Sleepcycle
     */
    public function setDatefrom($datefrom)
    {
        $this->datefrom = $datefrom;

        return $this;
    }

    /**
     * Get datefrom
     *
     * @return \DateTime
     */
    public function getDatefrom()
    {
        return $this->datefrom;
    }

    /**
     * Set dateto
     *
     * @param \DateTime $dateto
     *
     * @return Sleepcycle
     */
    public function setDateto($dateto)
    {
        $this->dateto = $dateto;

        return $this;
    }

    /**
     * Get dateto
     *
     * @return \DateTime
     */
    public function getDateto()
    {
        return $this->dateto;
    }

    /**
     * Set patientid
     *
     * @param \Application\Entity\Patient $patientid
     *
     * @return Sleepcycle
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
